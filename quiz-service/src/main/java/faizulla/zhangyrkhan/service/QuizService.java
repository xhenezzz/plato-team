package faizulla.zhangyrkhan.service;


import faizulla.zhangyrkhan.dto.request.CreateQuizRequest;
import faizulla.zhangyrkhan.dto.request.UpdateQuizRequest;
import faizulla.zhangyrkhan.dto.response.QuizResponse;

public interface QuizService {
    QuizResponse createQuiz(CreateQuizRequest request, Long teacherId);
    QuizResponse getQuizById(Long id);
    QuizResponse updateQuiz(Long quizId, UpdateQuizRequest request, Long teacherId);
    void deleteQuiz(Long quizId, Long teacherId);
    QuizResponse publishQuiz(Long quizId, Long teacherId);
}
