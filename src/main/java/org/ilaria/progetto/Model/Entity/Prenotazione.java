package org.ilaria.progetto.Model.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Data
@Entity
@Table(name = "prenotazioni")
public class Prenotazione {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataPrenotazione;
    private boolean approvata = false;
    private LocalTime durata;
    private Integer codice = 0;

    @Version
    private int version;

    @ManyToOne
    private Aula aula;

    @ManyToOne
    private Utente utente;

    public String toString(){
        return "Prenotazione [id=" + id + ", dataPrenotazione=" + dataPrenotazione +
                ", durata=" + durata + ", approvata=" + approvata +
                ", aulaId=" + (aula != null ? aula.getId() : "null") +
                ", utenteId=" + (utente != null ? utente.getId() : "null") + "]";
    }

}

