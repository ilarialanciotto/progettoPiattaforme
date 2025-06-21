package org.ilaria.progetto.Model.Entity;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "contenuti")
public class Contenuto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @ManyToOne
    @JoinColumn(name = "aula_id")
    private Aula aula;
}
