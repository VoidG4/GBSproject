package com.gbs.gbsproject.dao;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class QuestionDaoTest {

    @Test
    void testCreateQuestion() {
        try (MockedStatic<Database> mockedDatabase = mockStatic(Database.class)) {
            // Arrange
            int expectedId = 42;
            int quizId = 1;
            String text = "What is Java?";
            String type = "multiple-choice";

            mockedDatabase.when(() -> Database.executeInsertQuery(anyString(), eq(quizId), eq(text), eq(type)))
                    .thenReturn(expectedId);

            QuestionDao dao = new QuestionDao();

            // Act
            int actualId = dao.createQuestion(quizId, text, type);

            // Assert
            assertEquals(expectedId, actualId);
        }
    }
}
