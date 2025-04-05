package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Course;
import com.gbs.gbsproject.util.DatabaseUtil;

import java.sql.*;
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

    public boolean insertCourse(Course course) {
        String sql = "INSERT INTO course (id, name, description, tutor_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, course.getId());
            statement.setString(2, course.getName());
            statement.setString(3, course.getDescription());
            statement.setInt(4, course.getTutorId());

            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0; // If the insert was successful
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while loading the login page", e);
            return false;
        }
    }

    public boolean checkIfCourseIdExists(int courseId) {
        String sql = "SELECT COUNT(*) FROM course WHERE id = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, courseId); // Set the course ID parameter
            ResultSet resultSet = statement.executeQuery();
            resultSet.next(); // Move to the result row
            return resultSet.getInt(1) == 0; // If count is 0, the ID is unique
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while loading the login page", e);
            return false; // In case of an error, assume the ID is not unique
        }
    }

    // Method to fetch all courses for a specific tutor
    public static List<Course> getCoursesByTutorId(int tutorId) {
        List<Course> courses = new ArrayList<>();
        String query = "SELECT * FROM course WHERE tutor_id = ?"; // SQL query to fetch courses by tutor_id

        try (Connection conn = DatabaseUtil.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, tutorId); // Set tutor_id parameter in the query

            ResultSet rs = stmt.executeQuery(); // Execute the query
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                Course course = new Course(id, name, description, tutorId); // Create Course object
                courses.add(course); // Add the course to the list
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred", e);
        }

        return courses; // Return the list of courses
    }

    // Method to delete a course from the database
    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM course WHERE id = ?";

        try (PreparedStatement preparedStatement = DatabaseUtil.getConnection().prepareStatement(sql)) {
            preparedStatement.setInt(1, courseId);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred", e);
            return false;
        }
    }

}
