package org.ilaria.progetto.Model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
public class PrenotazioneAuleDTO {

    private long id;
    private LocalDate dataPrenotazione;
    private LocalTime oraPrenotazione;
    private LocalTime durata;
}
