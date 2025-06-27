package org.ilaria.progetto.Model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
