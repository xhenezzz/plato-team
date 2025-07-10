package faizulla.zhangyrkhan.repository;

import faizulla.zhangyrkhan.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    List<QuestionEntity> findAllByQuizId(Long quizId);
}
