package org.ilaria.progetto.Model.Entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "aule")
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cubo;
    private int numeroPosti,piano;
    private boolean laboratorio;

    @Version
    private int version;

    @ManyToOne
    @JoinColumn(name = "utente_id")
    private Utente responsabile;

}
