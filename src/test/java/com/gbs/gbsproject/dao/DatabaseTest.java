package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.util.DatabaseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;

import java.sql.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class DatabaseTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() {
        // Initialize the mocks
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        // Mock DatabaseUtil to return mockConnection
        try (MockedStatic<DatabaseUtil> dbUtilMock = mockStatic(DatabaseUtil.class)) {
            dbUtilMock.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

            // Ensure Database.connection is properly mocked, not null
            Database.setConnection(mockConnection);
        }
    }

    @Test
    public void testExecuteInsertQuery_success() throws SQLException {
        // Given
        String query = "INSERT INTO course (name, description, tutor_id) VALUES (?, ?, ?)";
        Object[] params = {"Course 1", "Description 1", 101};
        int generatedId = 1;

        // Mock the DatabaseUtil to return the mock connection
        try (MockedStatic<DatabaseUtil> dbUtilMock = mockStatic(DatabaseUtil.class)) {
            dbUtilMock.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

            // Mock the behavior of prepared statement and result set
            when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);
            when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(generatedId);

            // When: Call the method under test
            int result = Database.executeInsertQuery(query, params);

            // Then: Verify the results and interactions
            assertEquals(generatedId, result);  // Ensure the returned ID is correct
            verify(mockPreparedStatement).setObject(1, "Course 1");
            verify(mockPreparedStatement).setObject(2, "Description 1");
            verify(mockPreparedStatement).setObject(3, 101);
            verify(mockPreparedStatement).executeUpdate();  // Ensure the update was executed
        }
    }

    @Test
    public void testExecuteInsertQuery_failure() throws SQLException {
        // Given
        String query = "INSERT INTO course (name, description, tutor_id) VALUES (?, ?, ?)";
        Object[] params = {"Course 1", "Description 1", 101};

        // Mock the DatabaseUtil to return the mock connection
        try (MockedStatic<DatabaseUtil> dbUtilMock = mockStatic(DatabaseUtil.class)) {
            dbUtilMock.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

            // Mock the behavior of prepared statement
            when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);
            when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false); // Simulate no generated keys

            // When: Call the method under test
            int result = Database.executeInsertQuery(query, params);

            // Then: Verify the results and interactions
            assertEquals(-1, result);  // Ensure failure returns -1
        }
    }

    @Test
    public void testExecuteUpdateQuery_success() throws SQLException {
        // Given
        String query = "UPDATE course SET name = ?, description = ? WHERE id = ?";
        Object[] params = {"Updated Course", "Updated Description", 1};

        // Mock the DatabaseUtil to return the mock connection
        try (MockedStatic<DatabaseUtil> dbUtilMock = mockStatic(DatabaseUtil.class)) {
            dbUtilMock.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

            // Mock the behavior of prepared statement
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1);

            // When: Call the method under test
            Database.executeUpdateQuery(query, params);

            // Then: Verify the results and interactions
            verify(mockPreparedStatement).setObject(1, "Updated Course");
            verify(mockPreparedStatement).setObject(2, "Updated Description");
            verify(mockPreparedStatement).setObject(3, 1);
            verify(mockPreparedStatement).executeUpdate();  // Ensure the update was executed
        }
    }

    @Test
    public void testExecuteUpdateQuery_failure() throws SQLException {
        // Given
        String query = "UPDATE course SET name = ?, description = ? WHERE id = ?";
        Object[] params = {"Updated Course", "Updated Description", 1};

        // Mock the DatabaseUtil to return the mock connection
        try (MockedStatic<DatabaseUtil> dbUtilMock = mockStatic(DatabaseUtil.class)) {
            dbUtilMock.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

            // Mock the behavior of prepared statement
            when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("Database error"));

            // When: Call the method under test
            Database.executeUpdateQuery(query, params);  // This should log the exception, but not throw it

            // Then: Verify the behavior
            verify(mockPreparedStatement).setObject(1, "Updated Course");
            verify(mockPreparedStatement).setObject(2, "Updated Description");
            verify(mockPreparedStatement).setObject(3, 1);
            verify(mockPreparedStatement).executeUpdate();  // Ensure the update was attempted
        }
    }
}
