package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Tutor;
import com.gbs.gbsproject.util.DatabaseUtil;
import com.gbs.gbsproject.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
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
        String checkSQL = "SELECT COUNT(*) FROM tutor WHERE username = ? OR email = ?";
        String insertSQL = "INSERT INTO tutor (name, surname, username, password, email, field, salt) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection()) {

            // Step 1: Check if username or email already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
                checkStmt.setString(1, tutor.getUsername());
                checkStmt.setString(2, tutor.getEmail());
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    System.out.println("Username or email already exists. Tutor not added.");
                    return; // Exit early
                }
            }

            // Step 2: Generate salt and hash password
            byte[] salt = PasswordUtil.generateSalt();
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashedPassword = PasswordUtil.hashPassword(tutor.getPassword(), salt);

            // Step 3: Insert the new tutor
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                insertStmt.setString(1, tutor.getName());
                insertStmt.setString(2, tutor.getSurname());
                insertStmt.setString(3, tutor.getUsername());
                insertStmt.setString(4, hashedPassword);
                insertStmt.setString(5, tutor.getEmail());
                insertStmt.setString(6, tutor.getField());
                insertStmt.setString(7, saltBase64);

                insertStmt.executeUpdate();
                System.out.println("Tutor added successfully.");
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while adding tutor", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "An error occurred while hashing password", e);
        }
    }

    public static void updatePassword(Tutor tutor, String newPassword, String oldPassword) throws SQLException {
        String selectSQL = "SELECT password, salt FROM tutor WHERE id = ?";
        String updateSQL = "UPDATE tutor SET password = ?, salt = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection()) {

            // Step 1: Retrieve the old password hash and salt from the database
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {
                selectStmt.setInt(1, tutor.getId());
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
                                updateStmt.setInt(3, tutor.getId());         // Tutor ID

                                int rowsAffected = updateStmt.executeUpdate();

                                if (rowsAffected > 0) {
                                    System.out.println("Password updated successfully in database.");
                                } else {
                                    System.out.println("No tutor found with the given ID.");
                                }
                            }
                        } else {
                            System.out.println("Old password is incorrect.");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("No tutor found with the given ID.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
        }
    }

    public static void updateEmail(Tutor tutor, String newEmail) throws SQLException {
        String checkSQL = "SELECT COUNT(*) FROM tutor WHERE email = ?";
        String updateSQL = "UPDATE tutor SET email = ? WHERE id = ?";

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

            // Step 2: Update email
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                updateStmt.setString(1, newEmail);
                updateStmt.setInt(2, tutor.getId());

                int rowsAffected = updateStmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Email updated successfully in database.");
                } else {
                    System.out.println("No tutor found with the given ID.");
                }
            }

        }
    }

}
