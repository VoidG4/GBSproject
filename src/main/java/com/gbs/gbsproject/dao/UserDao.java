package com.gbs.gbsproject.dao;
import com.gbs.gbsproject.model.*;
import com.gbs.gbsproject.util.DatabaseUtil;
import com.gbs.gbsproject.util.PasswordUtil;

import java.sql.*;

public class UserDao {

    public static User checkLogin(String username, String password) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {
            // Check admin
            PreparedStatement adminStmt = conn.prepareStatement(
                    "select id, name, surname, email, password, salt from admin where username = ?"
            );
            adminStmt.setString(1, username);
            ResultSet rsAdmin = adminStmt.executeQuery();
            if (rsAdmin.next()) {
                String storedPasswordHash = rsAdmin.getString("password");
                String storedSalt = rsAdmin.getString("salt");

                // Verify the password by hashing the entered password with the stored salt
                try {
                    if (PasswordUtil.verifyPassword(password, storedPasswordHash, storedSalt)) {
                        return new Admin(rsAdmin.getInt("id"), rsAdmin.getString("name"), rsAdmin.getString("surname"), username, storedPasswordHash, rsAdmin.getString("email"));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            // Check tutor
            PreparedStatement tutorStmt = conn.prepareStatement(
                    "select id, name, surname, email, password, salt, field from tutor where username = ?;"
            );
            tutorStmt.setString(1, username);
            ResultSet rsTutor = tutorStmt.executeQuery();
            if (rsTutor.next()) {
                String storedPasswordHash = rsTutor.getString("password");
                String storedSalt = rsTutor.getString("salt");

                // Verify the password by hashing the entered password with the stored salt
                try {
                    if (PasswordUtil.verifyPassword(password, storedPasswordHash, storedSalt)) {
                        return new Tutor(rsTutor.getInt("id"), rsTutor.getString("name"), rsTutor.getString("surname"), username, storedPasswordHash, rsTutor.getString("email"), rsTutor.getString("field"));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            // Check student
            PreparedStatement studentStmt = conn.prepareStatement(
                    "select id, name, surname, email, password, salt from student where username = ?;"
            );
            studentStmt.setString(1, username);
            ResultSet rsStudent = studentStmt.executeQuery();
            if (rsStudent.next()) {
                String storedPasswordHash = rsStudent.getString("password");
                String storedSalt = rsStudent.getString("salt");

                // Verify the password by hashing the entered password with the stored salt
                try {
                    if (PasswordUtil.verifyPassword(password, storedPasswordHash, storedSalt)) {
                        return new Student(rsStudent.getInt("id"), rsStudent.getString("name"), rsStudent.getString("surname"), username, storedPasswordHash, rsStudent.getString("email"));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            return null; // Login failed
        }
    }
}
