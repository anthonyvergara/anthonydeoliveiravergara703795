package com.anthony.backend.domain.repository;

import com.anthony.backend.domain.model.AlbumImage;

import java.util.List;
import java.util.Optional;

public interface AlbumImageRepository {

    AlbumImage save(AlbumImage albumImage);

    Optional<AlbumImage> findById(Long id);

    List<AlbumImage> findAll();

    List<AlbumImage> findByAlbumId(Long albumId);

    void deleteById(Long id);

    boolean existsById(Long id);

    void updateIsDefaultByAlbumId(Long albumId, boolean isDefault);
}
