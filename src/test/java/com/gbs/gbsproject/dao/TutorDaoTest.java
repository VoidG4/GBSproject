package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Tutor;
import com.gbs.gbsproject.util.DatabaseUtil;
import com.gbs.gbsproject.util.PasswordUtil;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.sql.*;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TutorDaoTest {

    private Connection mockConnection;
    private Statement mockStatement;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private MockedStatic<DatabaseUtil> mockedDatabaseUtil;
    private MockedStatic<PasswordUtil> mockedPasswordUtil;

    @BeforeEach
    void setUp() {
        mockConnection = mock(Connection.class);
        mockStatement = mock(Statement.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        mockedDatabaseUtil = mockStatic(DatabaseUtil.class);
        mockedDatabaseUtil.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

        mockedPasswordUtil = mockStatic(PasswordUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockedDatabaseUtil.close();
        mockedPasswordUtil.close();
    }

    @Test
    void testGetAllTutors() throws Exception {
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery("SELECT * FROM tutor")).thenReturn(mockResultSet);

        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("Alice");
        when(mockResultSet.getString("surname")).thenReturn("Smith");
        when(mockResultSet.getString("username")).thenReturn("alice123");
        when(mockResultSet.getString("email")).thenReturn("alice@example.com");
        when(mockResultSet.getString("field")).thenReturn("Physics");

        List<Tutor> tutors = TutorDao.getAllTutors();
        assertEquals(1, tutors.size());
        assertEquals("Alice", tutors.getFirst().getName());
    }

    @Test
    void testAddTutor_NewTutor() throws Exception {
        Tutor tutor = new Tutor();
        tutor.setName("Alice");
        tutor.setSurname("Smith");
        tutor.setUsername("alice123");
        tutor.setPassword("securepass");
        tutor.setEmail("alice@example.com");
        tutor.setField("Physics");

        PreparedStatement checkStmt = mock(PreparedStatement.class);
        PreparedStatement insertStmt = mock(PreparedStatement.class);
        ResultSet checkRs = mock(ResultSet.class);

        when(mockConnection.prepareStatement(contains("SELECT COUNT"))).thenReturn(checkStmt);
        when(mockConnection.prepareStatement(contains("INSERT INTO tutor"))).thenReturn(insertStmt);
        when(checkStmt.executeQuery()).thenReturn(checkRs);
        when(checkRs.next()).thenReturn(true);
        when(checkRs.getInt(1)).thenReturn(0);

        byte[] salt = "randomSalt".getBytes();
        mockedPasswordUtil.when(PasswordUtil::generateSalt).thenReturn(salt);
        mockedPasswordUtil.when(() -> PasswordUtil.hashPassword("securepass", salt)).thenReturn("hashedPwd");

        TutorDao.addTutor(tutor);
        verify(insertStmt, times(1)).executeUpdate();
    }

    @Test
    void testDeleteUser_Tutor() throws Exception {
        when(mockConnection.prepareStatement(contains("DELETE FROM tutor"))).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        TutorDao.deleteUser("alice123", "tutor");

        verify(mockPreparedStatement).setString(1, "alice123");
        verify(mockPreparedStatement).executeUpdate();
    }

    @Test
    void testUpdatePassword_Success() throws Exception {
        Tutor tutor = new Tutor();
        tutor.setId(1);

        PreparedStatement selectStmt = mock(PreparedStatement.class);
        PreparedStatement updateStmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(mockConnection.prepareStatement(contains("SELECT password"))).thenReturn(selectStmt);
        when(mockConnection.prepareStatement(contains("UPDATE tutor"))).thenReturn(updateStmt);
        when(selectStmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("password")).thenReturn("oldHash");
        when(rs.getString("salt")).thenReturn("oldSalt");

        mockedPasswordUtil.when(() -> PasswordUtil.verifyPassword("oldPass", "oldHash", "oldSalt")).thenReturn(true);
        mockedPasswordUtil.when(PasswordUtil::generateSalt).thenReturn("newSalt".getBytes());
        mockedPasswordUtil.when(() -> PasswordUtil.hashPassword("newPass", "newSalt".getBytes())).thenReturn("newHash");

        when(updateStmt.executeUpdate()).thenReturn(1);

        TutorDao.updatePassword(tutor, "newPass", "oldPass");

        verify(updateStmt).setString(1, "newHash");
        verify(updateStmt).setString(2, Base64.getEncoder().encodeToString("newSalt".getBytes()));
        verify(updateStmt).setInt(3, 1);
    }

    @Test
    void testUpdateEmail_Success() throws Exception {
        Tutor tutor = new Tutor();
        tutor.setId(1);

        PreparedStatement checkStmt = mock(PreparedStatement.class);
        PreparedStatement updateStmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(mockConnection.prepareStatement(contains("SELECT COUNT"))).thenReturn(checkStmt);
        when(mockConnection.prepareStatement(contains("UPDATE tutor"))).thenReturn(updateStmt);
        when(checkStmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getInt(1)).thenReturn(0);

        when(updateStmt.executeUpdate()).thenReturn(1);

        TutorDao.updateEmail(tutor, "new@email.com");

        verify(updateStmt).setString(1, "new@email.com");
        verify(updateStmt).setInt(2, 1);
    }
}
