package com.anthony.backend.infrastructure.persistence.repository;

import com.anthony.backend.application.mapper.AlbumImageMapper;
import com.anthony.backend.domain.model.AlbumImage;
import com.anthony.backend.domain.repository.AlbumImageRepository;
import com.anthony.backend.infrastructure.persistence.entity.AlbumImageEntity;
import com.anthony.backend.infrastructure.persistence.jpa.AlbumImageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AlbumImageRepositoryImpl implements AlbumImageRepository {

    private final AlbumImageJpaRepository albumImageJpaRepository;
    private final AlbumImageMapper albumImageMapper;

    @Override
    public AlbumImage save(AlbumImage albumImage) {
        AlbumImageEntity entity = albumImageMapper.toEntity(albumImage);
        AlbumImageEntity savedEntity = albumImageJpaRepository.save(entity);
        return albumImageMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<AlbumImage> findById(Long id) {
        return albumImageJpaRepository.findById(id)
                .map(albumImageMapper::toDomain);
    }

    @Override
    public List<AlbumImage> findAll() {
        return albumImageJpaRepository.findAll().stream()
                .map(albumImageMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<AlbumImage> findByAlbumId(Long albumId) {
        return albumImageJpaRepository.findByAlbumId(albumId).stream()
                .map(albumImageMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        albumImageJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return albumImageJpaRepository.existsById(id);
    }
}

