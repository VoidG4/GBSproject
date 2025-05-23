package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.SectionContent;
import com.gbs.gbsproject.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SectionContentDao {
    private static final Logger LOGGER = Logger.getLogger(SectionContentDao.class.getName());

    public void insertSectionContent(int sectionId, String title, String contentType, String content, int contentOrder) {
        String query = "INSERT INTO section_content (section_id, title, content_type, content, content_order) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, sectionId);
            stmt.setString(2, title);
            stmt.setString(3, contentType);
            stmt.setString(4, content);
            stmt.setInt(5, contentOrder);
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred", e);
        }
    }

    public static void updateSectionContent(SectionContent content) throws SQLException {
        String updateQuery = "UPDATE section_content SET title = ?, content = ?, content_type = ?, content_order = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateQuery)) {

            // Set parameters for the update query
            ps.setString(1, content.title());
            ps.setString(2, content.content());
            ps.setString(3, content.contentType());
            ps.setInt(4, content.contentOrder());
            ps.setInt(5, content.id());

            // Execute the update
            ps.executeUpdate();
        }
    }

    // Assuming you have a method like this in your DAO class
    public static void deleteSectionContent(int contentId) throws SQLException {
        String deleteQuery = "DELETE FROM section_content WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteQuery)) {

            ps.setInt(1, contentId);
            ps.executeUpdate();
        }
    }

    // Method to fetch content by section ID and title
    public static SectionContent getContentBySectionIdAndTitle(int sectionId, String title) throws SQLException {
        String query = "SELECT * FROM section_content WHERE section_id = ? AND title = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, sectionId);
            ps.setString(2, title);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new SectionContent(rs.getInt("id"), rs.getInt("section_id"),
                        rs.getString("title"), rs.getString("content_type"),
                        rs.getString("content"), rs.getInt("content_order"));
            }
        }
        return null;
    }
    
    public static List<SectionContent> getContentBySectionId(int sectionId) throws SQLException {
        List<SectionContent> sectionContents = new ArrayList<>();

        String query = "SELECT * FROM section_content WHERE section_id = ?";

        try (PreparedStatement ps = DatabaseUtil.getConnection().prepareStatement(query)) {
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SectionContent content = new SectionContent(
                        rs.getInt("id"),
                        rs.getInt("section_id"),
                        rs.getString("title"),
                        rs.getString("content_type"),
                        rs.getString("content"),
                        rs.getInt("content_order")
                );
                sectionContents.add(content);
            }
        }

        return sectionContents;
    }

    public static List<SectionContent> getContentsForSection(int sectionId) throws SQLException {
        List<SectionContent> contents = new ArrayList<>();
        String query = "SELECT * FROM section_content WHERE section_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String title = rs.getString("title");
                    String contentType = rs.getString("content_type");
                    String content = rs.getString("content");
                    int contentOrder = rs.getInt("content_order");

                    SectionContent sectionContent = new SectionContent(id, sectionId, title, contentType, content, contentOrder);
                    contents.add(sectionContent);
                }
            }
        }
        return contents;
    }


}
