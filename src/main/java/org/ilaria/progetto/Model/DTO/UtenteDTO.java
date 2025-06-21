package org.ilaria.progetto.Model.DTO;

import lombok.Data;
import org.ilaria.progetto.Ruolo;

@Data
public class UtenteDTO {
    private String nome;
    private String email;
    private String password;
    private String codiceDocente;
    private long IdinAula;
    private Ruolo ruolo;

}
