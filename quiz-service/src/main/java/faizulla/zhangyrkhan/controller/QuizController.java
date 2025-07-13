package faizulla.zhangyrkhan.controller;

import faizulla.zhangyrkhan.dto.request.CreateQuizRequest;
import faizulla.zhangyrkhan.dto.request.UpdateQuizRequest;
import faizulla.zhangyrkhan.dto.response.QuizResponse;
import faizulla.zhangyrkhan.excrption.ResourceNotFoundException;
import faizulla.zhangyrkhan.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class   QuizController {
    private final QuizService quizService;

    // Создание квиза
    @PostMapping
    public ResponseEntity<QuizResponse> createQuiz(
            @Valid @RequestBody CreateQuizRequest request) {
        try {
            // TODO: Получать teacherId из токена
            Long teacherId = 1L;
            QuizResponse response = quizService.createQuiz(request, teacherId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Получение квиза по ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable Long id) {
        try {
            QuizResponse response = quizService.getQuizById(id);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Обновление квиза
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuiz(
            @PathVariable Long id,
            @Valid @RequestBody UpdateQuizRequest request) {
        try {
            // TODO: Получать teacherId из токена
            Long teacherId = 1L;
            QuizResponse response = quizService.updateQuiz(id, request, teacherId);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Удаление квиза
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable Long id) {
        try {
            // TODO: Получать teacherId из токена
            Long teacherId = 1L;
            quizService.deleteQuiz(id, teacherId);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // Публикация квиза
    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publishQuiz(@PathVariable Long id) {
        try {
            // TODO: Получать teacherId из токена
            Long teacherId = 1L;
            QuizResponse response = quizService.publishQuiz(id, teacherId);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}