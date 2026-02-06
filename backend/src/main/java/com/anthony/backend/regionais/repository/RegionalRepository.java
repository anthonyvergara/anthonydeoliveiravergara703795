package com.anthony.backend.regionais.repository;

import com.anthony.backend.regionais.domain.Regional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegionalRepository extends JpaRepository<Regional, Long> {

    List<Regional> findByAtivoTrue();

    @Query("SELECT r FROM Regional r WHERE r.ativo = true")
    List<Regional> findAllAtivas();
}

