package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Student;
import com.gbs.gbsproject.util.DatabaseUtil;
import com.gbs.gbsproject.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentDao {
    private static final Logger LOGGER = Logger.getLogger(StudentDao.class.getName());

    public static List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM student")) {

            while (rs.next()) {
                Student s = new Student(rs.getInt("id"), rs.getString("name"));
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setSurname(rs.getString("surname"));
                s.setUsername(rs.getString("username"));
                s.setEmail(rs.getString("email"));
                students.add(s);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred ", e);
        }
        return students;
    }

    public static String getFullNameByUsername(String username) {
        String fullName = "Student Name"; // Default in case of failure

        String query = "SELECT name, surname FROM student WHERE username = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    fullName = rs.getString("name") + " " + rs.getString("surname");
                }
            }
        } catch (SQLException e) {
            // Log error
            Logger.getLogger(StudentDao.class.getName()).log(Level.SEVERE, null, e);
        }

        return fullName;
    }

    public static void addStudent(Student student) {
        String sql = "INSERT INTO student (name, surname, username, password, email, salt) VALUES (?, ?, ?, ?, ?, ?);";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getName());
            pstmt.setString(2, student.getSurname());
            pstmt.setString(3, student.getUsername());
            pstmt.setString(4, student.getPassword());
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getSalt());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while adding tutor", e);
        }
    }

    public static void addQuizGrade(int student_id, int quiz_id, int score, boolean passed) {
        String selectSql = "SELECT score FROM student_quiz WHERE student_id = ? AND quiz_id = ?";
        String updateSql = "UPDATE student_quiz SET score = ?, passed = ? WHERE student_id = ? AND quiz_id = ?";
        String insertSql = "INSERT INTO student_quiz (student_id, quiz_id, score, passed) VALUES (?, ?, ?, ?)";

        // These will help us update the GPA in student_course
        String getCourseIdSql = "SELECT course_id FROM quiz WHERE id = ?";
        String gpaSql = "SELECT AVG(score) AS avg_score FROM student_quiz WHERE student_id = ? AND quiz_id IN (SELECT id FROM quiz WHERE course_id = ?)";
        String selectCourseSql = "SELECT grade FROM student_course WHERE student_id = ? AND course_id = ?";
        String updateCourseSql = "UPDATE student_course SET grade = ? WHERE student_id = ? AND course_id = ?";
        String insertCourseSql = "INSERT INTO student_course (student_id, course_id, grade) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection()) {

            // STEP 1: Insert or update the quiz grade
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setInt(1, student_id);
                selectStmt.setInt(2, quiz_id);

                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    int existingScore = rs.getInt("score");

                    if (score > existingScore) {
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setInt(1, score);
                            updateStmt.setBoolean(2, passed);
                            updateStmt.setInt(3, student_id);
                            updateStmt.setInt(4, quiz_id);
                            updateStmt.executeUpdate();
                        }
                    }
                } else {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, student_id);
                        insertStmt.setInt(2, quiz_id);
                        insertStmt.setInt(3, score);
                        insertStmt.setBoolean(4, passed);
                        insertStmt.executeUpdate();
                    }
                }
            }

            // STEP 2: Get course_id from quiz_id
            int course_id;
            try (PreparedStatement courseStmt = conn.prepareStatement(getCourseIdSql)) {
                courseStmt.setInt(1, quiz_id);
                ResultSet rs = courseStmt.executeQuery();
                if (rs.next()) {
                    course_id = rs.getInt("course_id");
                } else {
                    LOGGER.warning("No course found for quiz_id = " + quiz_id);
                    return;
                }
            }

            // STEP 3: Calculate GPA (average score for all quizzes in the course)
            int averageScore = 0;
            try (PreparedStatement gpaStmt = conn.prepareStatement(gpaSql)) {
                gpaStmt.setInt(1, student_id);
                gpaStmt.setInt(2, course_id);
                ResultSet rs = gpaStmt.executeQuery();
                if (rs.next()) {
                    averageScore = (int) Math.round(rs.getDouble("avg_score"));
                }
            }

            // STEP 4: Insert or update student_course with GPA
            try (PreparedStatement selectCourseStmt = conn.prepareStatement(selectCourseSql)) {
                selectCourseStmt.setInt(1, student_id);
                selectCourseStmt.setInt(2, course_id);

                ResultSet rs = selectCourseStmt.executeQuery();

                if (rs.next()) {
                    try (PreparedStatement updateCourseStmt = conn.prepareStatement(updateCourseSql)) {
                        updateCourseStmt.setInt(1, averageScore);
                        updateCourseStmt.setInt(2, student_id);
                        updateCourseStmt.setInt(3, course_id);
                        updateCourseStmt.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertCourseStmt = conn.prepareStatement(insertCourseSql)) {
                        insertCourseStmt.setInt(1, student_id);
                        insertCourseStmt.setInt(2, course_id);
                        insertCourseStmt.setInt(3, averageScore);
                        insertCourseStmt.executeUpdate();
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "An error occurred while adding/updating quiz grade and GPA", e);
        }
    }

    public static double getGrade(int course_id, int student_id) {
        double grade = 0;
        String query = "SELECT grade FROM student_course WHERE course_id = ? and student_id = ?";
        try (PreparedStatement stmt = DatabaseUtil.getConnection().prepareStatement(query)) {
            stmt.setInt(1, course_id);
            stmt.setInt(2, student_id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    grade = rs.getInt("grade");
                }
            }
        } catch (SQLException e) {
            // Log error
            Logger.getLogger(StudentDao.class.getName()).log(Level.SEVERE, null, e);
        }

        return grade;
    }

    public static void updatePassword(Student student, String newPassword, String oldPassword) throws SQLException {
        String selectSQL = "SELECT password, salt FROM student WHERE id = ?";
        String updateSQL = "UPDATE student SET password = ?, salt = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection()) {

            // Step 1: Retrieve the old password hash and salt from the database
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSQL)) {
                selectStmt.setInt(1, student.getId());
                ResultSet rs = selectStmt.executeQuery();

                if (rs.next()) {
                    String storedPasswordHash = rs.getString("password");
                    String storedSalt = rs.getString("salt");

                    // Step 2: Verify the old password
                    try {
                        if (PasswordUtil.verifyPassword(oldPassword, storedPasswordHash, storedSalt)) {

                            // Step 3: Generate a new salt and hash the new password
                            byte[] newSalt = PasswordUtil.generateSalt();
                            String newSaltBase64 = Base64.getEncoder().encodeToString(newSalt);
                            String newHashedPassword = PasswordUtil.hashPassword(newPassword, newSalt);

                            // Step 4: Update the password and salt in the database
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                                updateStmt.setString(1, newHashedPassword);  // New hashed password
                                updateStmt.setString(2, newSaltBase64);      // New salt
                                updateStmt.setInt(3, student.getId());       // Student ID

                                int rowsAffected = updateStmt.executeUpdate();

                                if (rowsAffected > 0) {
                                    System.out.println("Password updated successfully in database.");
                                } else {
                                    System.out.println("No student found with the given ID.");
                                }
                            }
                        } else {
                            System.out.println("Old password is incorrect.");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("No student found with the given ID.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error updating password: " + e.getMessage());
        }
    }


    public static void updateEmail(Student student, String newEmail) throws SQLException {
        String updateSQL = "UPDATE student SET email = ? WHERE id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateSQL)) {

            stmt.setString(1, newEmail);
            stmt.setInt(2, student.getId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Email updated successfully in database.");
            } else {
                System.out.println("No student found with the given ID.");
            }
        }
    }
}
