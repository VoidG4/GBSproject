package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.util.DatabaseUtil;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {
    private static final Logger LOGGER = Logger.getLogger(Database.class.getName());

    private static Connection connection;

    static {
        try {
            connection = DatabaseUtil.getConnection();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }
    }

    public static int executeInsertQuery(String query, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }
        return -1;
    }

    public static void executeUpdateQuery(String query, Object... params) {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }
    }
}