package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Course;
import com.gbs.gbsproject.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CourseDao {
    private static final Logger LOGGER = Logger.getLogger(CourseDao.class.getName());

    public static List<Course> getAllCourses() {
        List<Course> courseList = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM course")) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                int tutorId = rs.getInt("tutor_id");

                courseList.add(new Course(id, name, description, tutorId));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }
        return courseList;
    }
}
