package com.anthony.backend.infrastructure.persistence.jpa;

import com.anthony.backend.infrastructure.persistence.entity.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArtistJpaRepository extends JpaRepository<ArtistEntity, Long>, JpaSpecificationExecutor<ArtistEntity> {

    Optional<ArtistEntity> findByName(String name);
}

