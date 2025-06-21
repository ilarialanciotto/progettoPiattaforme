package org.ilaria.progetto.Repository;

import org.ilaria.progetto.Model.Entity.Utente;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UtenteRepository extends JpaRepository<Utente, Long> {

    @Query("SELECT U FROM Utente U  WHERE U.id=:id")
    Utente findById(@Param("id") long id);

    @Query("SELECT U FROM Utente U WHERE U.email=:email")
    Optional<Utente> findByEmail(@Param("email") String email);

    boolean existsByEmail(@Param("email") String email);

    @Modifying
    @Query("UPDATE Utente U SET U.idInAula=:idAula WHERE U.id=:id")
    void update(@Param("id") long id,  @Param("idAula") long idAula);

    @Modifying
    @Query("UPDATE Utente U SET U.email=:nuovaEmail , U.password=:nuovaPassword WHERE U.id=:id")
    void updateDati(@Param("id") long id, @Param("nuovaEmail") String nuovaEmail, @Param("nuovaPassword") String nuovaPassword);
}
