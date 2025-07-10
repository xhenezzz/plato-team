package faizulla.zhangyrkhan.dto.response;

import faizulla.zhangyrkhan.entity.enums.QuestionType;

public class QuestionResponse {
    private Long id;
    private Long quizId;
    private String questionText;
    private QuestionType questionType;
    private Integer points;
    private Integer orderIndex;
}
