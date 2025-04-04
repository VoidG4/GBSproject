package com.gbs.gbsproject.dao;
import com.gbs.gbsproject.model.*;
import com.gbs.gbsproject.util.DatabaseUtil;

import java.sql.*;

public class UserDao {
    public static User checkLogin(String username, String password) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection()) {

            // Check admin
            PreparedStatement adminStmt = conn.prepareStatement(
                    "select id, name from admin where username = ? and password = ?"
            );
            adminStmt.setString(1, username);
            adminStmt.setString(2, password);
            ResultSet rsAdmin = adminStmt.executeQuery();
            if (rsAdmin.next()) {
                return new Admin(rsAdmin.getInt("id"), rsAdmin.getString("name"));
            }

            // Check tutor
            PreparedStatement tutorStmt = conn.prepareStatement(
                    "select id, name from tutor where username = ? and password = ?"
            );
            tutorStmt.setString(1, username);
            tutorStmt.setString(2, password);
            ResultSet rsTutor = tutorStmt.executeQuery();
            if (rsTutor.next()) {
                return new Tutor(rsTutor.getInt("id"), rsTutor.getString("name"));
            }

            // Check student
            PreparedStatement studentStmt = conn.prepareStatement(
                    "select id, name from student where username = ? and password = ?"
            );
            studentStmt.setString(1, username);
            studentStmt.setString(2, password);
            ResultSet rsStudent = studentStmt.executeQuery();
            if (rsStudent.next()) {
                return new Student(rsStudent.getInt("id"), rsStudent.getString("name"));
            }

            return null; // Login failed
        }
    }
}
