package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Chat;
import com.gbs.gbsproject.model.Message;
import com.gbs.gbsproject.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeminiDao {
    private static final Logger LOGGER = Logger.getLogger(GeminiDao.class.getName());

    // Method to get the chat history for a specific user
    public List<Chat> getChatHistory(int userId) {
        List<Chat> chatHistory = new ArrayList<>();
        String query = "SELECT id, title FROM chat WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int chatId = resultSet.getInt("id");
                String chatTitle = resultSet.getString("title");
                chatHistory.add(new Chat(chatId, chatTitle));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }

        return chatHistory;
    }

    // Method to get messages of a specific chat
    public List<Message> getChatMessages(int chatId) {
        List<Message> messages = new ArrayList<>();
        String query = "SELECT sender, message FROM message WHERE chat_id = ? ORDER BY created_at ASC";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, chatId);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String sender = resultSet.getString("sender");
                String message = resultSet.getString("message");
                messages.add(new Message(sender, message));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }

        return messages;
    }

    // Method to delete a chat by its ID
    public void deleteChat(int chatId) {
        String deleteQuery = "DELETE FROM chat WHERE id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

            preparedStatement.setInt(1, chatId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }
    }

    public static int createNewChat(int studentId) throws SQLException {
        String sql = "INSERT INTO chat (user_id, title) VALUES (?, 'New chat') RETURNING id";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new SQLException("Failed to create new chat.");
            }
        }
    }

    public static void saveMessage(int chatId, String sender, String message) throws SQLException {
        String sql = "INSERT INTO message (chat_id, sender, message) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, chatId);
            stmt.setString(2, sender); // "user" or "gemini"
            stmt.setString(3, message);
            stmt.executeUpdate();
        }
    }

    public static void setChatTitle(int chatId, String title) throws SQLException {
        String sql = "UPDATE chat SET title = ? WHERE id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, title);
            stmt.setInt(2, chatId);
            stmt.executeUpdate();
        }
    }

}
