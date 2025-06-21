package org.ilaria.progetto.Service.Mapper;

import org.ilaria.progetto.Model.DTO.AulaDTO;
import org.ilaria.progetto.Model.Entity.Aula;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ContenutoMapper.class)
public interface AulaMapper {

    @Mapping(target = "responsabile", ignore = true)
    AulaDTO toDto(Aula aula);

    @Mapping(target = "responsabile", ignore = true)
    @Mapping(target = "version", ignore = true)
    Aula toEntity(AulaDTO aulaDTO);
}