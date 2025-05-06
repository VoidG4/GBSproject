package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Admin;
import com.gbs.gbsproject.util.DatabaseUtil;
import com.gbs.gbsproject.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

import static org.mockito.Mockito.*;

public class AdminDaoTest {

    private Connection mockConnection;
    private PreparedStatement mockCheckStmt;
    private PreparedStatement mockSelectStmt;
    private PreparedStatement mockUpdateStmt;
    private ResultSet mockResultSet;

    @BeforeEach
    void setup() throws Exception {
        mockConnection = mock(Connection.class);
        mockSelectStmt = mock(PreparedStatement.class);
        mockUpdateStmt = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockCheckStmt = mock(PreparedStatement.class);

        when(mockConnection.prepareStatement(anyString()))
                .thenReturn(mockSelectStmt)
                .thenReturn(mockUpdateStmt);
    }

    @Test
    void testUpdatePassword_success() throws Exception {
        Admin mockAdmin = new Admin();
        mockAdmin.setId(1);

        String oldPassword = "oldPass";
        String newPassword = "newPass";
        String storedHash = "storedHash";
        String storedSalt = "storedSalt";

        byte[] newSalt = new byte[]{1, 2, 3};
        String newSaltBase64 = Base64.getEncoder().encodeToString(newSalt);
        String newHashedPassword = "hashedNewPass";

        // Simulate DB returning a row with password and salt
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("password")).thenReturn(storedHash);
        when(mockResultSet.getString("salt")).thenReturn(storedSalt);

        when(mockSelectStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockUpdateStmt.executeUpdate()).thenReturn(1);

        try (
                MockedStatic<DatabaseUtil> dbMock = mockStatic(DatabaseUtil.class);
                MockedStatic<PasswordUtil> pwMock = mockStatic(PasswordUtil.class)
        ) {
            dbMock.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

            // Mock password verification and generation
            pwMock.when(() -> PasswordUtil.verifyPassword(oldPassword, storedHash, storedSalt)).thenReturn(true);
            pwMock.when(PasswordUtil::generateSalt).thenReturn(newSalt);
            pwMock.when(() -> PasswordUtil.hashPassword(newPassword, newSalt)).thenReturn(newHashedPassword);

            AdminDao.updatePassword(mockAdmin, newPassword, oldPassword);

            // Check parameter setting for update
            verify(mockUpdateStmt).setString(1, newHashedPassword);
            verify(mockUpdateStmt).setString(2, newSaltBase64);
            verify(mockUpdateStmt).setInt(3, mockAdmin.getId());
        }
    }

    @Test
    void testUpdateEmail_successfulUpdate() throws Exception {
        Admin mockAdmin = new Admin();
        mockAdmin.setId(1);

        String newEmail = "new@example.com";

        // 🧪 Set up mocks for email check
        when(mockConnection.prepareStatement("SELECT COUNT(*) FROM admin WHERE email = ?"))
                .thenReturn(mockCheckStmt);
        when(mockCheckStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt(1)).thenReturn(0); // Email not in use

        // 🧪 Set up mocks for update
        when(mockConnection.prepareStatement("UPDATE admin SET email = ? WHERE id = ?"))
                .thenReturn(mockUpdateStmt);
        when(mockUpdateStmt.executeUpdate()).thenReturn(1); // Simulate success

        // 💡 Mock DatabaseUtil.getConnection()
        try (MockedStatic<DatabaseUtil> dbUtilMock = mockStatic(DatabaseUtil.class)) {
            dbUtilMock.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

            // 🔥 Call method under test
            AdminDao.updateEmail(mockAdmin, newEmail);

            // ✅ Verify correct SQL flow
            verify(mockCheckStmt).setString(1, newEmail);
            verify(mockUpdateStmt).setString(1, newEmail);
            verify(mockUpdateStmt).setInt(2, 1);
        }
    }
}
