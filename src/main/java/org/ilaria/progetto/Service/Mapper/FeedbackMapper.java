package org.ilaria.progetto.Service.Mapper;

import org.ilaria.progetto.Model.DTO.FeedbackDTO;
import org.ilaria.progetto.Model.Entity.Feedback;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface FeedbackMapper {

    FeedbackDTO toDto(Feedback feedback);

    @Mapping(target = "id", ignore = true)
    Feedback toEntity(FeedbackDTO feedbackDTO);

}
