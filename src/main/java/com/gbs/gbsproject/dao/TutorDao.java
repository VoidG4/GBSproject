package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Tutor;
import com.gbs.gbsproject.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TutorDao {
    private static final Logger LOGGER = Logger.getLogger(TutorDao.class.getName());

    public static List<Tutor> getAllTutors() {
        List<Tutor> tutors = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tutor")) {

            while (rs.next()) {
                Tutor t = new Tutor(rs.getInt("id"), rs.getString("name"));
                t.setId(rs.getInt("id"));
                t.setName(rs.getString("name"));
                t.setSurname(rs.getString("surname"));
                t.setUsername(rs.getString("username"));
                t.setEmail(rs.getString("email"));
                t.setField(rs.getString("field"));
                tutors.add(t);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }
        return tutors;
    }

    public static void deleteUser(String username, String role) {
        String sql = "";
        if (role.equals("tutor")) {
            sql = "DELETE FROM tutor WHERE username = ?";
        } else if (role.equals("student")) {
            sql = "DELETE FROM student WHERE username = ?";
        }


        try (PreparedStatement preparedStatement = DatabaseUtil.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, username);
            int _ = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred", e);
        }
    }

    public static void addTutor(Tutor tutor) {
        String sql = "INSERT INTO tutor (name, surname, username, password, email, field) VALUES (?, ?, ?, ?, ?, ?);";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tutor.getName());
            pstmt.setString(2, tutor.getSurname());
            pstmt.setString(3, tutor.getUsername());
            pstmt.setString(4, tutor.getPassword());
            pstmt.setString(5, tutor.getEmail());
            pstmt.setString(6, tutor.getField());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while adding tutor", e);
        }
    }

    public static void updatePassword(Tutor tutor, String newPassword, String oldPassword) throws SQLException {
        String updateSQL = "UPDATE tutor SET password = ? WHERE id = ? AND password = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSQL)) {

            stmt.setString(1, newPassword);
            stmt.setInt(2, tutor.getId());
            stmt.setString(3, oldPassword);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Password updated successfully in database.");
            } else {
                System.out.println("No admin found with the given ID.");
            }
        }
    }

    public static void updateEmail(Tutor tutor, String newEmail) throws SQLException {
        String updateSQL = "UPDATE tutor SET email = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSQL)) {

            stmt.setString(1, newEmail);
            stmt.setInt(2, tutor.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Email updated successfully in database.");
            } else {
                System.out.println("No admin found with the given ID.");
            }
        }
    }
}
