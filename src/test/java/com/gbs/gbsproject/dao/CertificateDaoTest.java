package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Certificate;
import com.gbs.gbsproject.util.DatabaseUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CertificateDaoTest {

    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private CertificateDao certificateDao;

    @BeforeEach
    void setup() {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);

        certificateDao = new CertificateDao();
    }

    @Test
    void testSaveCertificate_success() throws SQLException {
        // Given
        Certificate certificate = new Certificate("John Doe", LocalDate.of(2023, 5, 1));

        // Mock DatabaseUtil to return the mocked connection
        try (MockedStatic<DatabaseUtil> dbUtilMock = mockStatic(DatabaseUtil.class)) {
            dbUtilMock.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

            // Mock PreparedStatement behavior
            when(mockConnection.prepareStatement(any(String.class), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
                    .thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate successful execution
            when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(true);
            when(mockResultSet.getInt(1)).thenReturn(100); // Simulate returning a generated ID of 100

            // When
            int generatedId = certificateDao.saveCertificate(certificate);

            // Then
            verify(mockPreparedStatement).setString(1, "John Doe");
            verify(mockPreparedStatement).setDate(2, Date.valueOf(LocalDate.of(2023, 5, 1)));
            verify(mockPreparedStatement).executeUpdate();
            verify(mockResultSet).next();
            verify(mockResultSet).getInt(1);
            assert(generatedId == 100);
        }
    }

    @Test
    void testSaveCertificate_failure_noRowsAffected() throws SQLException {
        // Given
        Certificate certificate = new Certificate("John Doe", LocalDate.of(2023, 5, 1));

        // Mock DatabaseUtil to return the mocked connection
        try (MockedStatic<DatabaseUtil> dbUtilMock = mockStatic(DatabaseUtil.class)) {
            dbUtilMock.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

            // Mock PreparedStatement behavior
            when(mockConnection.prepareStatement(any(String.class), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
                    .thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(0); // Simulate failure (no rows affected)

            // When
            int generatedId = certificateDao.saveCertificate(certificate);

            // Then
            verify(mockPreparedStatement).setString(1, "John Doe");
            verify(mockPreparedStatement).setDate(2, Date.valueOf(LocalDate.of(2023, 5, 1)));
            verify(mockPreparedStatement).executeUpdate();
            assert(generatedId == -1); // Simulate the failure return value
        }
    }

    @Test
    void testSaveCertificate_failure_noGeneratedId() throws SQLException {
        // Given
        Certificate certificate = new Certificate("John Doe", LocalDate.of(2023, 5, 1));

        // Mock DatabaseUtil to return the mocked connection
        try (MockedStatic<DatabaseUtil> dbUtilMock = mockStatic(DatabaseUtil.class)) {
            dbUtilMock.when(DatabaseUtil::getConnection).thenReturn(mockConnection);

            // Mock PreparedStatement behavior
            when(mockConnection.prepareStatement(any(String.class), eq(PreparedStatement.RETURN_GENERATED_KEYS)))
                    .thenReturn(mockPreparedStatement);
            when(mockPreparedStatement.executeUpdate()).thenReturn(1); // Simulate successful execution
            when(mockPreparedStatement.getGeneratedKeys()).thenReturn(mockResultSet);
            when(mockResultSet.next()).thenReturn(false); // Simulate no generated key

            // When
            int generatedId = certificateDao.saveCertificate(certificate);

            // Then
            verify(mockPreparedStatement).setString(1, "John Doe");
            verify(mockPreparedStatement).setDate(2, Date.valueOf(LocalDate.of(2023, 5, 1)));
            verify(mockPreparedStatement).executeUpdate();
            verify(mockResultSet).next();
            assert(generatedId == -1); // Simulate failure when no generated key is returned
        }
    }
}
