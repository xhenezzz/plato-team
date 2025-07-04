package faizulla.zhangyrkhan.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateQuizRequest {
    @Size(min = 3, max = 100, message = "Название квиза должно быть от {min} до {max} символов")
    private String title;

    @Size(max = 1000, message = "Описание квиза не должно превышать {max} символов")
    private String description;

    @Positive(message = "ID категории должен быть больше 0")
    private Long categoryId;

    @PositiveOrZero(message = "Время лимита должно быть неотрицательным")
    private Integer timeLimit;

    @Min(value = 1, message = "Количество попыток должно быть больше 0")
    private Integer maxAttempts;

    @FutureOrPresent(message = "Дата начала квиза не может быть в прошлом")
    private LocalDateTime availableFrom;

    @Future(message = "Дата окончания квиза не может быть в прошлом")
    private LocalDateTime availableTo;

    @AssertTrue(message = "Дата окончания должна быть позже даты начала")
    public boolean isDateRangeValid() {
        if (availableFrom == null || availableTo == null) {
            return true; // null-значения обрабатываются другими аннотациями
        }
        return availableTo.isAfter(availableFrom);
    }
}