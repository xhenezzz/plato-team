package faizulla.zhangyrkhan.mapper;

import faizulla.zhangyrkhan.dto.request.CreateQuizRequest;
import faizulla.zhangyrkhan.dto.response.QuizResponse;
import faizulla.zhangyrkhan.entity.QuizEntity;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface QuizMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacherId", source = "teacherId")
    @Mapping(target = "published", constant = "false")
    @Mapping(target = "maxAttempts", defaultValue = "1")
    QuizEntity toEntity(CreateQuizRequest request, @Context Long teacherId);
    QuizResponse toResponse(QuizEntity entity);
}
