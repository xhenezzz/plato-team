package controller;

import faizulla.zhangyrkhan.controller.QuizController;
import faizulla.zhangyrkhan.dto.request.CreateQuizRequest;
import faizulla.zhangyrkhan.dto.response.QuizResponse;
import faizulla.zhangyrkhan.excrption.ResourceNotFoundException;
import faizulla.zhangyrkhan.service.QuizService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class QuizControllerTest {

    @Mock
    private QuizService quizService;

    @InjectMocks
    private QuizController quizController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getQuizById_ShouldReturnQuiz_WhenExists() {
        // Arrange
        Long quizId = 1L;
        QuizResponse expectedResponse = new QuizResponse();
        when(quizService.getQuizById(quizId)).thenReturn(expectedResponse);

        // Act
        ResponseEntity<?> response = quizController.getQuizById(quizId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    void getQuizById_ShouldReturnNotFound_WhenNotExists() {
        // Arrange
        Long quizId = 999L;
        when(quizService.getQuizById(quizId))
                .thenThrow(new ResourceNotFoundException("Quiz", quizId));

        // Act
        ResponseEntity<?> response = quizController.getQuizById(quizId);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("не найден"));
    }

    @Test
    void createQuiz_ShouldReturnCreatedQuiz() {
        // Arrange
        CreateQuizRequest request = new CreateQuizRequest(
                "Test Quiz",
                "Description",
                1L, 30, 3,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2)
        );

        QuizResponse expectedResponse = new QuizResponse();
        when(quizService.createQuiz(any(), anyLong())).thenReturn(expectedResponse);

        // Act
        ResponseEntity<QuizResponse> response = quizController.createQuiz(request, "1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
    }
}