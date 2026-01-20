package com.anthony.backend.domain.repository;

import com.anthony.backend.domain.model.Album;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository {

    Album save(Album album);

    Optional<Album> findById(Long id);

    List<Album> findAll();

    List<Album> findByArtistId(Long artistId);

    Optional<Album> findByTitle(String title);

    void deleteById(Long id);

    boolean existsById(Long id);
}

