package org.ilaria.progetto.Repository;

import org.ilaria.progetto.Model.Entity.Aula;
import org.ilaria.progetto.Model.Entity.Contenuto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ContenutoRepository extends JpaRepository<Contenuto, Long> {

    @Query("SELECT C FROM Contenuto C WHERE C.aula=:aula ORDER BY C.id ASC")
    List<Contenuto> findByAula(@Param("aula") Aula aula);

}
