package org.ilaria.progetto.Repository;

import org.ilaria.progetto.Model.Entity.Content;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ContentRepository extends JpaRepository<Content, Long> { }
