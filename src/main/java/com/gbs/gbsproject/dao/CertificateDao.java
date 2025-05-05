package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Certificate;
import com.gbs.gbsproject.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CertificateDao {
    private static final Logger LOGGER = Logger.getLogger(CertificateDao.class.getName());


    public int saveCertificate(Certificate certificate) {
        String sql = "INSERT INTO certificate (full_name, issue_date) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            // Set the parameters – convert LocalDate to java.sql.Date
            ps.setString(1, certificate.fullName());
            ps.setDate(2, Date.valueOf(certificate.issueDate()));

            int affectedRows = ps.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Inserting certificate failed, no rows affected.");
            }

            // Retrieve the generated key (certificate id)
            try (var generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Inserting certificate failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error inserting certificate into the database", e);
            return -1;
        }
    }
}
