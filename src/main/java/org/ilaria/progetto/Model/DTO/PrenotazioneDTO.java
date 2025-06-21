package org.ilaria.progetto.Model.DTO;

import lombok.Data;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class PrenotazioneDTO {

    private long id;
    private LocalDateTime dataPrenotazione;
    private LocalTime durata;
    private Integer codice=0;
    private long idutente;
    private long idlaboratorio;

}
