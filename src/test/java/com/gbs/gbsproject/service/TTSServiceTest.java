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
    void testGenerateSpeech_whenAudioNotPlaying_shouldStartAudio() throws Exception {
        // Arrange
        String text = "Hello world";

        // Mock the ExecutorService to do nothing when submit() is called
        when(mockExecutorService.submit(any(Runnable.class))).thenReturn(null);

        // Mock the ProcessBuilder to return a mocked process
        when(mockProcessBuilder.start()).thenReturn(mockProcess);

        // Mock the Process to simulate successful execution
        when(mockProcess.waitFor()).thenReturn(0);  // Simulate successful process completion

        // Mock the static method in TTSService to use our mocked ExecutorService and ProcessBuilder
        TTSService.setExecutorService(mockExecutorService);
        TTSService.setVlcProcess(mockProcess);

        // Act
        TTSService.generateSpeech(text);

        // Assert
        assertTrue(TTSService.getIsPlaying());  // Verify that audio is playing
        verify(mockProcessBuilder).start();  // Ensure the process is started
        verify(mockProcess).waitFor();  // Ensure process waitFor is called
    }

    @Test
    void testGenerateSpeech_whenAudioAlreadyPlaying_shouldNotStartNewAudio() {
        // Arrange
        TTSService.setIsPlaying(true);  // Simulate that audio is already playing

        // Act
        TTSService.generateSpeech("New audio");

        // Assert
        assertFalse(TTSService.getIsPlaying());  // Ensure the audio doesn't start
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
