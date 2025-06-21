package org.ilaria.progetto.Service.Mapper;

import org.ilaria.progetto.Model.DTO.PrenotazioneDTO;
import org.ilaria.progetto.Model.Entity.Prenotazione;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PrenotazioneMapper {

    PrenotazioneDTO toDto(Prenotazione prenotazione);

    @Mapping(target = "aula", ignore = true)
    @Mapping(target = "utente", ignore = true)
    @Mapping(target = "id", ignore = true)
    Prenotazione toEntity(PrenotazioneDTO prenotazioneDTO);

}
