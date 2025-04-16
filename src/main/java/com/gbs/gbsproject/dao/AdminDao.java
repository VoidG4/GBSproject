package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Admin;
import com.gbs.gbsproject.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminDao {
    public static void updatePassword(Admin admin, String newPassword, String oldPassword) throws SQLException {
        String updateSQL = "UPDATE admin SET password = ? WHERE id = ? AND password = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSQL)) {

            stmt.setString(1, newPassword);
            stmt.setInt(2, admin.getId());
            stmt.setString(3, oldPassword);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Password updated successfully in database.");
            } else {
                System.out.println("No admin found with the given ID.");
            }
        }
    }

    public static void updateEmail(Admin admin, String newEmail) throws SQLException {
        String updateSQL = "UPDATE admin SET email = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSQL)) {

            stmt.setString(1, newEmail);
            stmt.setInt(2, admin.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Email updated successfully in database.");
            } else {
                System.out.println("No admin found with the given ID.");
            }
        }
    }
}
