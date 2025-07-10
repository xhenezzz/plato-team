package faizulla.zhangyrkhan.dto.request;

import faizulla.zhangyrkhan.entity.enums.QuestionType;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateQuestionRequest {
    @Size(min = 1, max = 1000, message = "Текст вопроса должен содержать от 1 до 1000 символов")
    private String questionText;

    private QuestionType questionType;

    @Positive(message = "Количество баллов должно быть положительным числом")
    private Integer points;

    @PositiveOrZero(message = "Порядковый номер не может быть отрицательным")
    private Integer orderIndex;
}