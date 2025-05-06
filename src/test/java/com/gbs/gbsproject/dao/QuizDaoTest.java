package com.gbs.gbsproject.dao;

import com.gbs.gbsproject.model.Question;
import com.gbs.gbsproject.model.Quiz;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class QuizDaoTest {

    private static QuizDao quizDao;
    private static int createdQuizId;
    private static final int TEST_COURSE_ID = 1;  // Ensure this exists in test DB
    private static final int TEST_TUTOR_ID = 1;   // Ensure this exists and owns TEST_COURSE_ID

    @BeforeAll
    static void setup() {
        quizDao = new QuizDao();
    }

    @Test
    @Order(1)
    void testGetQuizzesByTutor() {
        List<Quiz> quizzes = quizDao.getQuizzesByTutor(TEST_TUTOR_ID);
        assertNotNull(quizzes);
    }

    @Test
    @Order(2)
    void testGetQuestionsByQuizId() throws SQLException {
        List<Question> questions = quizDao.getQuestionsByQuizId(createdQuizId);
        assertNotNull(questions);
    }

    @Test
    @Order(3)
    void testDeleteQuizById() throws SQLException {
        quizDao.deleteQuizById(createdQuizId);
        List<Quiz> quizzes = quizDao.getQuizzesByCourseId(TEST_COURSE_ID);
        assertTrue(quizzes.stream().noneMatch(q -> q.getId() == createdQuizId));
    }
}
