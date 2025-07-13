package service;

import faizulla.zhangyrkhan.dto.request.CreateQuizRequest;
import faizulla.zhangyrkhan.dto.response.QuizResponse;
import faizulla.zhangyrkhan.entity.QuizEntity;
import faizulla.zhangyrkhan.excrption.ResourceNotFoundException;
import faizulla.zhangyrkhan.mapper.QuizMapper;
import faizulla.zhangyrkhan.repository.QuizRepository;
import faizulla.zhangyrkhan.service.Impl.QuizServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class QuizServiceImplTest {

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private QuizMapper quizMapper;

    @InjectMocks
    private QuizServiceImpl quizService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getQuizById_ShouldReturnQuiz_WhenQuizExists() {
        // Arrange
        Long quizId = 1L;
        QuizEntity quizEntity = new QuizEntity();
        QuizResponse expectedResponse = new QuizResponse();

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quizEntity));
        when(quizMapper.toResponse(quizEntity)).thenReturn(expectedResponse);

        // Act
        QuizResponse result = quizService.getQuizById(quizId);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(quizRepository, times(1)).findById(quizId);
    }

    @Test
    void getQuizById_ShouldThrowException_WhenQuizNotExists() {
        // Arrange
        Long quizId = 999L;
        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> quizService.getQuizById(quizId));
        verify(quizRepository, times(1)).findById(quizId);
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

        QuizEntity savedEntity = new QuizEntity();
        QuizResponse expectedResponse = new QuizResponse();

        when(quizMapper.toEntity(any(), any())).thenReturn(new QuizEntity());
        when(quizRepository.save(any())).thenReturn(savedEntity);
        when(quizMapper.toResponse(savedEntity)).thenReturn(expectedResponse);

        // Act
        QuizResponse result = quizService.createQuiz(request, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(quizRepository, times(1)).save(any());
    }
}