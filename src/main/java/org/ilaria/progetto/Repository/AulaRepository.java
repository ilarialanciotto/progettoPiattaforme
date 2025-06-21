package org.ilaria.progetto.Repository;

import jakarta.persistence.LockModeType;
import org.ilaria.progetto.Model.Entity.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Optional;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long>{

    @Query("SELECT A FROM Aula A WHERE A.id=:id")
    Aula findById(@Param("id") long id);

    @Query("SELECT A FROM Aula A WHERE A.numeroPosti>0 ORDER BY A.id ASC")
    LinkedList<Aula> findLibere();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Aula a WHERE a.id = :id")
    Aula findByIdWithLock(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Aula a SET a.numeroPosti=:numeroPosti WHERE a.id = :id")
    void update(@Param("id") long id, @Param("numeroPosti") int numeroPosti);

}
