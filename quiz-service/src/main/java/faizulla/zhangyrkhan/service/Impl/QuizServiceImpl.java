package faizulla.zhangyrkhan.service.Impl;

import faizulla.zhangyrkhan.dto.request.CreateQuizRequest;
import faizulla.zhangyrkhan.dto.request.UpdateQuizRequest;
import faizulla.zhangyrkhan.dto.response.QuizResponse;
import faizulla.zhangyrkhan.entity.QuizEntity;
import faizulla.zhangyrkhan.excrption.ResourceNotFoundException;
import faizulla.zhangyrkhan.mapper.QuizMapper;
import faizulla.zhangyrkhan.repository.QuizRepository;
import faizulla.zhangyrkhan.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;
    private final QuizMapper quizMapper;

    @Override
    @Transactional
    public QuizResponse createQuiz(CreateQuizRequest request, Long teacherId) {
        if (request.getAvailableTo() != null &&
                request.getAvailableFrom() != null &&
                request.getAvailableTo().isBefore(request.getAvailableFrom())) {
            throw new IllegalArgumentException("Дата окончания должна быть после даты начала");
        }

        QuizEntity quiz = quizMapper.toEntity(request, teacherId);
        QuizEntity savedQuiz = quizRepository.save(quiz);
        return quizMapper.toResponse(savedQuiz);
    }

    @Override
    @Transactional(readOnly = true)
    public QuizResponse getQuizById(Long id) {
        return quizRepository.findById(id)
                .map(quizMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", id));
    }

    @Override
    @Transactional
    public QuizResponse updateQuiz(Long quizId, UpdateQuizRequest request, Long teacherId) {
        QuizEntity quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", quizId));

        // Проверяем, что квиз принадлежит учителю
        if (!Objects.equals(quiz.getTeacherId(), teacherId)) {
            throw new IllegalStateException("Вы не имеете прав на редактирование этого квиза");
        }

        // Обновляем только те поля, которые не null в запросе
        if (request.getTitle() != null) {
            quiz.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            quiz.setDescription(request.getDescription());
        }
        if (request.getCategoryId() != null) {
            quiz.setCategoryId(request.getCategoryId());
        }
        if (request.getTimeLimit() != null) {
            quiz.setTimeLimit(request.getTimeLimit());
        }
        if (request.getMaxAttempts() != null) {
            quiz.setMaxAttempts(request.getMaxAttempts());
        }
        if (request.getAvailableFrom() != null) {
            quiz.setAvailableFrom(request.getAvailableFrom());
        }
        if (request.getAvailableTo() != null) {
            quiz.setAvailableTo(request.getAvailableTo());
        }

        // Проверяем валидность дат
        if (quiz.getAvailableTo() != null &&
                quiz.getAvailableFrom() != null &&
                quiz.getAvailableTo().isBefore(quiz.getAvailableFrom())) {
            throw new IllegalArgumentException("Дата окончания должна быть после даты начала");
        }

        QuizEntity updatedQuiz = quizRepository.save(quiz);
        return quizMapper.toResponse(updatedQuiz);
    }

    @Override
    @Transactional
    public void deleteQuiz(Long quizId, Long teacherId) {
        QuizEntity quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", quizId));

        // Проверяем, что квиз принадлежит учителю
        if (!Objects.equals(quiz.getTeacherId(), teacherId)) {
            throw new IllegalStateException("Вы не имеете прав на удаление этого квиза");
        }

        quizRepository.delete(quiz);
    }

    @Override
    @Transactional
    public QuizResponse publishQuiz(Long quizId, Long teacherId) {
        QuizEntity quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", quizId));

        // Проверяем, что квиз принадлежит учителю
        if (!Objects.equals(quiz.getTeacherId(), teacherId)) {
            throw new IllegalStateException("Вы не имеете прав на публикацию этого квиза");
        }

        // Проверяем, что все обязательные поля заполнены
        if (quiz.getTitle() == null || quiz.getTitle().trim().isEmpty() ||
                quiz.getCategoryId() == null ||
                quiz.getAvailableFrom() == null || quiz.getAvailableTo() == null) {
            throw new IllegalStateException("Не все обязательные поля заполнены для публикации квиза");
        }

        // Проверяем даты
        if (quiz.getAvailableTo().isBefore(quiz.getAvailableFrom())) {
            throw new IllegalStateException("Дата окончания должна быть после даты начала");
        }

        quiz.setPublished(true);
        QuizEntity publishedQuiz = quizRepository.save(quiz);
        return quizMapper.toResponse(publishedQuiz);
    }
}
