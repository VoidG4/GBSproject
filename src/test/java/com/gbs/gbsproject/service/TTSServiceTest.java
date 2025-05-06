package com.gbs.gbsproject.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.concurrent.*;

public class TTSServiceTest {

    @Mock
    private ProcessBuilder mockProcessBuilder;

    @Mock
    private Process mockProcess;

    @Mock
    private ExecutorService mockExecutorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Initializes mocks
    }

    @Test
    void testStopAudio_whenAudioPlaying_shouldStopAudio() {
        // Arrange
        TTSService.setVlcProcess(mockProcess);  // Mock VLC process
        when(mockProcess.isAlive()).thenReturn(true);

        // Act
        TTSService.stopAudio();

        // Assert
        verify(mockProcess).destroy();  // Ensure VLC process is stopped
        assertFalse(TTSService.getIsPlaying());  // Ensure the playing status is updated
    }

    @Test
    void testStopAudio_whenNoAudioPlaying_shouldNotStopAudio() {
        // Arrange
        TTSService.setVlcProcess(null); // No process running

        // Act
        TTSService.stopAudio();

        // Assert
        verify(mockProcess, never()).destroy();  // Ensure no stop call is made
    }
}
