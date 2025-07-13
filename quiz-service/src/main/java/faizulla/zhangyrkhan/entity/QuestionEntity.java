package faizulla.zhangyrkhan.entity;

import faizulla.zhangyrkhan.entity.enums.QuestionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "question")
@Getter
@Setter
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long quizId;
    private String questionText;
    private QuestionType questionType;
    private Integer points;
    private Integer orderIndex;

}
