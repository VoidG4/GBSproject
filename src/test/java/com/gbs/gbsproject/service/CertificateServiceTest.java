package com.gbs.gbsproject.service;

import com.gbs.gbsproject.dao.CertificateDao;
import com.gbs.gbsproject.model.Certificate;
import org.junit.jupiter.api.*;
import org.mockito.MockedConstruction;

import java.io.File;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CertificateServiceTest {

    private CertificateService certificateService;

    @BeforeEach
    void setUp() {
        certificateService = new CertificateService();
    }

    @Test
    void testGenerateCertificate_Success() {
        // Arrange: create mock certificate
        Certificate mockCertificate = mock(Certificate.class);
        when(mockCertificate.fullName()).thenReturn("Jane Doe");
        when(mockCertificate.issueDate()).thenReturn(LocalDate.of(2025, 5, 1));

        // Mock CertificateDao constructor and behavior
        try (MockedConstruction<CertificateDao> _ = mockConstruction(CertificateDao.class,
                (mock, _) -> when(mock.saveCertificate(mockCertificate)).thenReturn(123))) {

            // Act
            String resultPath = certificateService.generateCertificate(mockCertificate);

            // Assert
            assertNotNull(resultPath);
            assertTrue(resultPath.endsWith("Certificate_of_Completion.pdf"));
            assertTrue(new File(resultPath).exists()); // Can be fragile in CI unless cleanup is handled
        }
    }

    @Test
    void testGenerateCertificate_Failure() {
        // Arrange
        Certificate mockCertificate = mock(Certificate.class);
        when(mockCertificate.fullName()).thenThrow(new RuntimeException("Broken"));

        // Act
        String result = certificateService.generateCertificate(mockCertificate);

        // Assert
        assertNull(result);
    }

    @Test
    void testOpenCertificate_SkipsOnNullPath() {
        assertDoesNotThrow(() -> certificateService.openCertificate(null));
    }
}
