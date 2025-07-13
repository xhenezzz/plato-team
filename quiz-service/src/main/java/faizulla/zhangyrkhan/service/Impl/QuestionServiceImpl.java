package faizulla.zhangyrkhan.service.Impl;

import faizulla.zhangyrkhan.dto.request.CreateQuestionRequest;
import faizulla.zhangyrkhan.dto.request.UpdateQuestionRequest;
import faizulla.zhangyrkhan.dto.response.QuestionResponse;
import faizulla.zhangyrkhan.entity.QuestionEntity;
import faizulla.zhangyrkhan.excrption.ResourceNotFoundException;
import faizulla.zhangyrkhan.mapper.QuestionMapper;
import faizulla.zhangyrkhan.repository.QuestionRepository;
import faizulla.zhangyrkhan.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    @Override
    public QuestionResponse createQuestion(CreateQuestionRequest createQuestionRequest) {
        QuestionEntity question = questionMapper.toEntity(createQuestionRequest);
        QuestionEntity savedQuestion = questionRepository.save(question);
        return questionMapper.toResponse(savedQuestion);
    }

    @Override
    public QuestionResponse updateQuestion(Long questionId, UpdateQuestionRequest updateQuestionRequest) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", questionId));

        if (updateQuestionRequest.getQuestionText() != null) {
            question.setQuestionText(updateQuestionRequest.getQuestionText());
        }
        if (updateQuestionRequest.getQuestionType() != null) {
            question.setQuestionType(updateQuestionRequest.getQuestionType());
        }
        if (updateQuestionRequest.getPoints() != null) {
            question.setPoints(updateQuestionRequest.getPoints());
        }
        if (updateQuestionRequest.getOrderIndex() != null) {
            question.setOrderIndex(updateQuestionRequest.getOrderIndex());
        }

        QuestionEntity updatedQuestion = questionRepository.save(question);
        return questionMapper.toResponse(updatedQuestion);
    }

    @Override
    public void deleteQuestion(Long questionId) {
        if (!questionRepository.existsById(questionId)) {
            throw new ResourceNotFoundException("Question", questionId);
        }
        questionRepository.deleteById(questionId);
    }

    @Override
    public List<QuestionResponse> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findAllByQuizId(quizId)
                .stream()
                .map(questionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionResponse getQuestionById(Long questionId) {
        QuestionEntity question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", questionId));
        return questionMapper.toResponse(question);
    }
}
