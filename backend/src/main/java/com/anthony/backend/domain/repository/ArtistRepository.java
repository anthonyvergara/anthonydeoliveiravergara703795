package com.anthony.backend.domain.repository;

import com.anthony.backend.domain.model.Artist;

import java.util.List;
import java.util.Optional;

public interface ArtistRepository {

    Artist save(Artist artist);

    Optional<Artist> findById(Long id);

    List<Artist> findAll();

    Optional<Artist> findByName(String name);

    void deleteById(Long id);

    boolean existsById(Long id);
}

