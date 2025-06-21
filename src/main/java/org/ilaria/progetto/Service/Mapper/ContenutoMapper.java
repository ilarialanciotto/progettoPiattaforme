package org.ilaria.progetto.Service.Mapper;

import org.ilaria.progetto.Model.DTO.ContenutoDTO;
import org.ilaria.progetto.Model.Entity.Contenuto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ContenutoMapper {

    ContenutoDTO toDto(Contenuto contenuto);

    @Mapping(target = "id", ignore = true)
    Contenuto toEntity(ContenutoDTO contenutoDTO);
}
