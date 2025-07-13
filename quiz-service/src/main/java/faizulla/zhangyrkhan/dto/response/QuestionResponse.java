package faizulla.zhangyrkhan.dto.response;

import faizulla.zhangyrkhan.entity.enums.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {
    private Long id;
    private Long quizId;
    private String questionText;
    private QuestionType questionType;
    private Integer points;
    private Integer orderIndex;
}
