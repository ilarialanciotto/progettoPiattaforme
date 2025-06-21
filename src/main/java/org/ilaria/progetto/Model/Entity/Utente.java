package org.ilaria.progetto.Model.Entity;

import jakarta.persistence.*;
import lombok.Data;
import org.ilaria.progetto.Ruolo;

@Data
@Entity
@Table(name = "utenti")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String password;
    private String codiceDocente;
    private long idInAula = -1;
    @Enumerated(EnumType.STRING)
    private Ruolo ruolo;

}


