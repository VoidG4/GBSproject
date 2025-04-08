package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Student;
import com.gbs.gbsproject.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentDao {
    private static final Logger LOGGER = Logger.getLogger(StudentDao.class.getName());

    public static List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM student")) {

            while (rs.next()) {
                Student s = new Student(rs.getInt("id"), rs.getString("name"));
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setSurname(rs.getString("surname"));
                s.setUsername(rs.getString("username"));
                s.setEmail(rs.getString("email"));
                students.add(s);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }
        return students;
    }

    public String getFullNameByUsername(String username) {
        String fullName = "Student Name"; // Default in case of failure

        String query = "SELECT name, surname FROM student WHERE username = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    fullName = rs.getString("name") + " " + rs.getString("surname");
                }
            }
        } catch (SQLException e) {
            // Log error
            Logger.getLogger(StudentDao.class.getName()).log(Level.SEVERE, null, e);
        }

        return fullName;
    }
}
