package com.anthony.backend.infrastructure.persistence.jpa;

import com.anthony.backend.infrastructure.persistence.entity.AlbumImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumImageJpaRepository extends JpaRepository<AlbumImageEntity, Long> {

    List<AlbumImageEntity> findByAlbumId(Long albumId);
}

