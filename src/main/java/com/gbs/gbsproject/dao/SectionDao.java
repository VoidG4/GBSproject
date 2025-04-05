package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Section;
import com.gbs.gbsproject.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SectionDao {
    private static final Logger LOGGER = Logger.getLogger(SectionDao.class.getName());

    public static List<Section> getSectionsByCourseName(String courseName) {
        List<Section> sections = new ArrayList<>();
        String query = "SELECT * FROM section WHERE course_id IN (SELECT id FROM course WHERE name = ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, courseName);  // Set the course name in the query

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                int courseId = rs.getInt("course_id");
                int sectionOrder = rs.getInt("section_order");

                // Create a Section object and add it to the list
                Section section = new Section(courseId, title, description, sectionOrder);
                section.setId(id); // Assuming you have a setId method in the Section class
                sections.add(section);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred", e);
        }

        return sections;
    }

    // Delete a section from the database
    public static void deleteSection(int sectionId) throws SQLException {
        String deleteQuery = "DELETE FROM section WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteQuery)) {

            // Set the section ID to delete
            ps.setInt(1, sectionId);

            // Execute the delete query
            ps.executeUpdate();
        }
    }

    public static void updateSection(int sectionId, String newTitle, String newDescription) throws SQLException {
        String updateSQL = "UPDATE section SET title = ?, description = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSQL)) {

            stmt.setString(1, newTitle);
            stmt.setString(2, newDescription);
            stmt.setInt(3, sectionId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Section updated successfully in database.");
            } else {
                System.out.println("No section found with the given ID.");
            }
        }
    }


    public static List<Section> getSectionsForCourse(int courseId) throws SQLException {
        List<Section> sections = new ArrayList<>();

        String sql = "SELECT * FROM section WHERE course_id = ? ORDER BY section_order";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Section section = new Section(
                        rs.getInt("id"),
                        rs.getInt("course_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("section_order")
                );
                sections.add(section);
            }
        }

        return sections;
    }


    public static Section insertSection(Section section) throws SQLException {
        String sql = "INSERT INTO section (course_id, title, description, section_order) " +
                "VALUES (?, ?, ?, ?) RETURNING *";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, section.getCourseId());
            stmt.setString(2, section.getTitle());
            stmt.setString(3, section.getDescription());
            stmt.setInt(4, section.getSectionOrder());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Construct new Section object from returned row
                return new Section(
                        rs.getInt("id"),
                        rs.getInt("course_id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("section_order")
                );
            } else {
                throw new SQLException("Failed to insert section, no result returned.");
            }
        }
    }
}