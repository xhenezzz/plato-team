package faizulla.zhangyrkhan.dto.request;

import faizulla.zhangyrkhan.entity.enums.QuestionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class CreateQuestionRequest {
    @NotNull(message = "ID викторины обязательно")
    private Long quizId;

    @NotBlank(message = "Текст вопроса не может быть пустым")
    private String questionText;

    @NotNull(message = "Тип вопроса обязателен")
    private QuestionType questionType;

    @NotNull(message = "Количество баллов обязательно")
    @Positive(message = "Количество баллов должно быть положительным числом")
    private Integer points;

    @NotNull(message = "Порядковый номер обязателен")
    @PositiveOrZero(message = "Порядковый номер не может быть отрицательным")
    private Integer orderIndex;
}