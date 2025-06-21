package org.ilaria.progetto.Model.DTO;

import lombok.Data;

import java.util.LinkedList;

@Data
public class AulaDTO {

    private long id;
    private String cubo;
    private int numeroPosti,piano;
    private boolean laboratorio;
    private LinkedList<ContenutoDTO> contenuti;
    private String responsabile;
}
