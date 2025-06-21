package org.ilaria.progetto.Repository;

import org.ilaria.progetto.Model.Entity.Prenotazione;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.LinkedList;

@Repository
public interface PrenotazioneRepository extends JpaRepository<Prenotazione, Long> {

    @Modifying
    @Query("DELETE Prenotazione p WHERE p.aula.id=:idAula and p.utente.id=:idUtente")
    void delete(@Param("idAula") Long idAula,@Param("idUtente") Long idUtente);

    @Query("SELECT P FROM Prenotazione P WHERE P.id=:id")
    Prenotazione findById(long id);

    @Query("SELECT P FROM Prenotazione P WHERE P.aula.responsabile.id=:id and P.approvata=false ORDER BY P.id ASC")
    LinkedList<Prenotazione> findPrenotazione(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Prenotazione p SET p.approvata = :approvata  WHERE p.id = :id")
    void accetta(@Param("id") Long id, @Param("approvata") boolean approvata);

    @Query("SELECT P FROM Prenotazione P WHERE P.aula.id=:aulaID ORDER BY P.id ASC")
    LinkedList<Prenotazione> esiste(@Param("aulaID") long aulaID);

    @Modifying
    @Query("UPDATE Prenotazione p SET p.codice=:codice WHERE p.id = :id")
    void update(@Param("codice") int codice,@Param("id") long id);

    @Query("SELECT p FROM Prenotazione p WHERE p.codice=:codice and p.aula.id=:idAula and p.utente.id=:id ORDER BY p.id ASC")
    LinkedList<Prenotazione> controllo(@Param("idAula")long idAula,@Param("codice")int codice,@Param("id")Long id);

    @Query("SELECT P FROM Prenotazione P WHERE P.utente.id=:id")
    LinkedList<Prenotazione> findbyUtente(@Param("id")Long id);

    @Modifying
    @Query("DELETE Prenotazione p WHERE p.id=:idPrenotazione")
    void delete(@Param("idPrenotazione")long idPrenotazione);
}
