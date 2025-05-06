package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.*;
import com.gbs.gbsproject.util.DatabaseUtil;
import com.gbs.gbsproject.util.PasswordUtil;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserDaoTest {

    private Connection mockConnection;
    private PreparedStatement mockStmt;
    private ResultSet mockResultSet;
    private MockedStatic<DatabaseUtil> mockedDatabaseUtil;
    private MockedStatic<PasswordUtil> mockedPasswordUtil;

    @BeforeEach
    void setUp() {
        mockConnection = mock(Connection.class);
        mockStmt = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        mockedDatabaseUtil = Mockito.mockStatic(DatabaseUtil.class);
        mockedDatabaseUtil.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

        mockedPasswordUtil = Mockito.mockStatic(PasswordUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockedDatabaseUtil.close();
        mockedPasswordUtil.close();
    }

    @Test
    void testCheckLogin_Admin_Success() throws Exception {
        when(mockConnection.prepareStatement(contains("from admin"))).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);

        // Admin mock data
        when(mockResultSet.getInt("id")).thenReturn(1);
        when(mockResultSet.getString("name")).thenReturn("John");
        when(mockResultSet.getString("surname")).thenReturn("Doe");
        when(mockResultSet.getString("email")).thenReturn("admin@example.com");
        when(mockResultSet.getString("password")).thenReturn("hashedPwd");
        when(mockResultSet.getString("salt")).thenReturn("salt123");

        // Password matches
        mockedPasswordUtil.when(() -> PasswordUtil.verifyPassword("password", "hashedPwd", "salt123"))
                .thenReturn(true);

        User result = UserDao.checkLogin("adminUser", "password");
        assertNotNull(result);
        assertInstanceOf(Admin.class, result);
        assertEquals("John", result.getName());
    }

    @Test
    void testCheckLogin_Tutor_Success() throws Exception {
        // Admin returns no match
        PreparedStatement adminStmt = mock(PreparedStatement.class);
        ResultSet adminRs = mock(ResultSet.class);
        when(mockConnection.prepareStatement(contains("from admin"))).thenReturn(adminStmt);
        when(adminStmt.executeQuery()).thenReturn(adminRs);
        when(adminRs.next()).thenReturn(false);

        // Tutor match
        PreparedStatement tutorStmt = mock(PreparedStatement.class);
        ResultSet tutorRs = mock(ResultSet.class);
        when(mockConnection.prepareStatement(contains("from tutor"))).thenReturn(tutorStmt);
        when(tutorStmt.executeQuery()).thenReturn(tutorRs);
        when(tutorRs.next()).thenReturn(true);

        when(tutorRs.getInt("id")).thenReturn(2);
        when(tutorRs.getString("name")).thenReturn("Jane");
        when(tutorRs.getString("surname")).thenReturn("Smith");
        when(tutorRs.getString("email")).thenReturn("tutor@example.com");
        when(tutorRs.getString("password")).thenReturn("hashedPwd");
        when(tutorRs.getString("salt")).thenReturn("salt456");
        when(tutorRs.getString("field")).thenReturn("Math");

        mockedPasswordUtil.when(() -> PasswordUtil.verifyPassword("password", "hashedPwd", "salt456"))
                .thenReturn(true);

        User result = UserDao.checkLogin("tutorUser", "password");
        assertNotNull(result);
        assertInstanceOf(Tutor.class, result);
        assertEquals("Jane", result.getName());
    }

    @Test
    void testCheckLogin_Student_Success() throws Exception {
        // Admin & Tutor return no match
        for (String userType : new String[]{"admin", "tutor"}) {
            PreparedStatement stmt = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);
            when(mockConnection.prepareStatement(contains("from " + userType))).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(false);
        }

        // Student match
        PreparedStatement studentStmt = mock(PreparedStatement.class);
        ResultSet studentRs = mock(ResultSet.class);
        when(mockConnection.prepareStatement(contains("from student"))).thenReturn(studentStmt);
        when(studentStmt.executeQuery()).thenReturn(studentRs);
        when(studentRs.next()).thenReturn(true);

        when(studentRs.getInt("id")).thenReturn(3);
        when(studentRs.getString("name")).thenReturn("Mike");
        when(studentRs.getString("surname")).thenReturn("Johnson");
        when(studentRs.getString("email")).thenReturn("student@example.com");
        when(studentRs.getString("password")).thenReturn("hashedPwd");
        when(studentRs.getString("salt")).thenReturn("salt789");

        mockedPasswordUtil.when(() -> PasswordUtil.verifyPassword("password", "hashedPwd", "salt789"))
                .thenReturn(true);

        User result = UserDao.checkLogin("studentUser", "password");
        assertNotNull(result);
        assertInstanceOf(Student.class, result);
        assertEquals("Mike", result.getName());
    }

    @Test
    void testCheckLogin_InvalidUser() throws Exception {
        for (String userType : new String[]{"admin", "tutor", "student"}) {
            PreparedStatement stmt = mock(PreparedStatement.class);
            ResultSet rs = mock(ResultSet.class);
            when(mockConnection.prepareStatement(contains("from " + userType))).thenReturn(stmt);
            when(stmt.executeQuery()).thenReturn(rs);
            when(rs.next()).thenReturn(false);
        }

        User result = UserDao.checkLogin("nonexistentUser", "wrongPassword");
        assertNull(result);
    }
}
