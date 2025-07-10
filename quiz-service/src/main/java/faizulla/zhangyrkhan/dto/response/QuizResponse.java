package faizulla.zhangyrkhan.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {
    private Long id;
    private String title;
    private String description;
    private Long categoryId;
    private Long teacherId;
    private Integer timeLimit;
    private Integer maxAttempts;
    private LocalDateTime availableFrom;
    private LocalDateTime availableTo;
    private boolean published;
}