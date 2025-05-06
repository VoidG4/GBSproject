package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Course;
import com.gbs.gbsproject.util.DatabaseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CourseDaoTest {

    private CourseDao courseDao;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;

    @BeforeEach
    void setup() {
        courseDao = new CourseDao();
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
    }

    @Test
    void testInsertCourse_success() throws SQLException {
        // Given
        Course course = new Course(1, "Course 1", "Description 1", 101);

        // Mock the DatabaseUtil to return the mock connection
        try (MockedStatic<DatabaseUtil> dbUtilMock = mockStatic(DatabaseUtil.class)) {
            dbUtilMock.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

            // Mock the PreparedStatement behavior
            when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate successful insert

            // When
            boolean isInserted = courseDao.insertCourse(course);

            // Then
            verify(mockPreparedStatement).setInt(1, 1);
            verify(mockPreparedStatement).setString(2, "Course 1");
            verify(mockPreparedStatement).setString(3, "Description 1");
            verify(mockPreparedStatement).setInt(4, 101);
            verify(mockPreparedStatement).executeUpdate();
            assert(isInserted);
        }
    }

    @Test
    void testCheckIfCourseIdExists_noCourseFound() throws SQLException {
        // Given
        int courseId = 1;

        // Mock the DatabaseUtil to return the mock connection
        try (MockedStatic<DatabaseUtil> dbUtilMock = mockStatic(DatabaseUtil.class)) {
            dbUtilMock.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

            // Mock the PreparedStatement behavior
            when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(0); // Simulate that course does not exist

            // When
            boolean isUnique = courseDao.checkIfCourseIdExists(courseId);

            // Then
            verify(mockPreparedStatement).setInt(1, courseId);
            assert(isUnique); // Should be true as the ID is unique
        }
    }

    @Test
    void testDeleteCourse_success() throws SQLException {
        // Given
        int courseId = 1;

        // Mock the DatabaseUtil to return the mock connection
        try (MockedStatic<DatabaseUtil> dbUtilMock = mockStatic(DatabaseUtil.class)) {
            dbUtilMock.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

            // Mock the PreparedStatement behavior
            when(mockConnection.prepareStatement(any(String.class))).thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate successful delete

            // When
            boolean isDeleted = courseDao.deleteCourse(courseId);

            // Then
            verify(mockPreparedStatement).setInt(1, courseId);
            verify(mockPreparedStatement).executeUpdate();
            assert(isDeleted);
        }
    }
}
