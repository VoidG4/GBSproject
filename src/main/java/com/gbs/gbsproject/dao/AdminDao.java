package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Admin;
import com.gbs.gbsproject.util.DatabaseUtil;
import com.gbs.gbsproject.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class AdminDao {

    public static void updatePassword(Admin admin, String newPassword, String oldPassword) throws SQLException {
        String selectSQL = "SELECT password, salt FROM admin WHERE id = ?";
        String updateSQL = "UPDATE admin SET password = ?, salt = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection()) {

            // Step 1: Retrieve the old password hash and salt from the database
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {
                selectStmt.setInt(1, admin.getId());
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    String storedPasswordHash = rs.getString("password");
                    String storedSalt = rs.getString("salt");

                    // Step 2: Verify the old password
                    try {
                        if (PasswordUtil.verifyPassword(oldPassword, storedPasswordHash, storedSalt)) {

                            // Step 3: Generate a new salt and hash the new password
                            byte[] newSalt = PasswordUtil.generateSalt();
                            String newSaltBase64 = Base64.getEncoder().encodeToString(newSalt);
                            String newHashedPassword = PasswordUtil.hashPassword(newPassword, newSalt);

                            // Step 4: Update the password and salt in the database
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                                updateStmt.setString(1, newHashedPassword);  // New hashed password
                                updateStmt.setString(2, newSaltBase64);      // New salt
                                updateStmt.setInt(3, admin.getId());         // Admin ID

                                int rowsAffected = updateStmt.executeUpdate();

                                if (rowsAffected > 0) {
                                    System.out.println("Password updated successfully in database.");
                                } else {
                                    System.out.println("No admin found with the given ID.");
                                }
                            }
                        } else {
                            System.out.println("Old password is incorrect.");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("No admin found with the given ID.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
        }
    }


    public static void updateEmail(Admin admin, String newEmail) throws SQLException {
        String checkSQL = "SELECT COUNT(*) FROM admin WHERE email = ?";
        String updateSQL = "UPDATE admin SET email = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection()) {

            // Step 1: Check if new email already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
                checkStmt.setString(1, newEmail);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Email already in use. Update aborted.");
                    return;
                }
            }

            // Step 2: Proceed with email update
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                updateStmt.setString(1, newEmail);
                updateStmt.setInt(2, admin.getId());

                int rowsAffected = updateStmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Email updated successfully in database.");
                } else {
                    System.out.println("No admin found with the given ID.");
                }
            }

        }
    }

}
