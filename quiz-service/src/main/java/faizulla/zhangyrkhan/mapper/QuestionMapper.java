package faizulla.zhangyrkhan.mapper;

import faizulla.zhangyrkhan.dto.request.CreateQuestionRequest;
import faizulla.zhangyrkhan.dto.response.QuestionResponse;
import faizulla.zhangyrkhan.entity.QuestionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface QuestionMapper {
        QuestionEntity toEntity(CreateQuestionRequest request);
        QuestionResponse toResponse(QuestionEntity entity);
}
