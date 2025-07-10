package faizulla.zhangyrkhan.service;

import faizulla.zhangyrkhan.dto.request.CreateQuestionRequest;
import faizulla.zhangyrkhan.dto.request.UpdateQuestionRequest;
import faizulla.zhangyrkhan.dto.response.QuestionResponse;

import java.util.List;

public interface QuestionService {
        QuestionResponse createQuestion(CreateQuestionRequest createQuestionRequest);
        QuestionResponse updateQuestion(Long questionId, UpdateQuestionRequest updateQuestionRequest);
        void deleteQuestion(Long questionId);
        List<QuestionResponse> getQuestionsByQuizId(Long quizId);
        QuestionResponse getQuestionById(Long questionId);
}
