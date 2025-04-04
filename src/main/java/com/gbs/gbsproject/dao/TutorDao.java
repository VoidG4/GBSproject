package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Tutor;
import com.gbs.gbsproject.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TutorDao {
    private static final Logger LOGGER = Logger.getLogger(TutorDao.class.getName());

    public static List<Tutor> getAllTutors() {
        List<Tutor> tutors = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM tutor")) {

            while (rs.next()) {
                Tutor t = new Tutor(rs.getInt("id"), rs.getString("name"));
                t.setId(rs.getInt("id"));
                t.setName(rs.getString("name"));
                t.setSurname(rs.getString("surname"));
                t.setUsername(rs.getString("username"));
                t.setEmail(rs.getString("email"));
                t.setField(rs.getString("field"));
                tutors.add(t);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }
        return tutors;
    }
}
