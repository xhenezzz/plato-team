package faizulla.zhangyrkhan.controller;

import faizulla.zhangyrkhan.dto.request.CreateQuestionRequest;
import faizulla.zhangyrkhan.dto.request.UpdateQuestionRequest;
import faizulla.zhangyrkhan.dto.response.QuestionResponse;
import faizulla.zhangyrkhan.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<QuestionResponse> createQuestion(
            @Valid @RequestBody CreateQuestionRequest request) {
        QuestionResponse response = questionService.createQuestion(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Long questionId,
            @Valid @RequestBody UpdateQuestionRequest request) {
        QuestionResponse response = questionService.updateQuestion(questionId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestion(@PathVariable Long questionId) {
        questionService.deleteQuestion(questionId);
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuestionResponse>> getQuestionsByQuizId(
            @PathVariable Long quizId) {
        List<QuestionResponse> questions = questionService.getQuestionsByQuizId(quizId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionResponse> getQuestionById(
            @PathVariable Long questionId) {
        QuestionResponse question = questionService.getQuestionById(questionId);
        return ResponseEntity.ok(question);
    }
}