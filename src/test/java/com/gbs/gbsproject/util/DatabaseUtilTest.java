package com.gbs.gbsproject.util;

import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class DatabaseUtilTest {

    @Test
    void testGetConnection_Success() throws SQLException {
        // Assumes you have a working config.properties and running PostgreSQL
        Connection conn = DatabaseUtil.getConnection();
        assertNotNull(conn);
        assertFalse(conn.isClosed());

        DatabaseUtil.closeConnection(conn);
        assertTrue(conn.isClosed());
    }

    @Test
    void testCloseConnection_NullSafe() {
        // Should not throw anything
        assertDoesNotThrow(() -> DatabaseUtil.closeConnection(null));
    }

    @Test
    void testCloseConnection_HandlesSQLException() throws SQLException {
        // Arrange
        Connection mockConn = mock(Connection.class);
        doThrow(new SQLException("Simulated close error")).when(mockConn).close();

        // Act & Assert
        assertDoesNotThrow(() -> DatabaseUtil.closeConnection(mockConn));
    }
}
