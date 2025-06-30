package org.ilaria.progetto.Repository;

import org.ilaria.progetto.Model.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT U FROM User U  WHERE U.id=:id")
    User findById(@Param("id") long id);

    @Query("SELECT U FROM User U WHERE U.email=:email")
    Optional<User> findByEmail(@Param("email") String email);

    boolean existsByEmail(@Param("email") String email);

    @Modifying
    @Query("UPDATE User U SET U.classroomIDin=:classroomID WHERE U.id=:id")
    void update(@Param("id") long id,  @Param("classroomID") long classroomID);

    @Modifying
    @Query("UPDATE User U SET U.email=:newEmail , U.password=:newPassword WHERE U.id=:id")
    void updateDates(@Param("id") long id, @Param("newEmail") String newEmail, @Param("newPassword") String newPassword);
}
