package org.ilaria.progetto.Service.Mapper;

import org.ilaria.progetto.Model.DTO.UtenteDTO;
import org.ilaria.progetto.Model.Entity.Utente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UtenteMapper {

    UtenteDTO toDto(Utente utente);

    @Mapping(target = "id", ignore = true)
    Utente toEntity(UtenteDTO utenteDTO);
}

