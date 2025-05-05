package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentCourseDao {
    private static final Logger LOGGER = Logger.getLogger(StudentCourseDao.class.getName());

    public static boolean hasPassedAllCourses(int studentId) {
        String sql = "SELECT COUNT(*) FROM student_course WHERE student_id = ? AND grade < 5";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0; // true if all grades > 5
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }
        return false; // default to false on error
    }
}
