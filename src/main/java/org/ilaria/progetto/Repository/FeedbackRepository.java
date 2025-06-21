package org.ilaria.progetto.Repository;

import org.ilaria.progetto.Model.Entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface  FeedbackRepository extends JpaRepository<Feedback, Long> {
}
