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

                // Print to check if the content is being fetched
                System.out.println("Fetched Content: " + content.title() + ", " + content.content());
            }
        }

        return sectionContents;
    }

}
