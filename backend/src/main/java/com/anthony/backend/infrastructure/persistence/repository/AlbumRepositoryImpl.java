package com.anthony.backend.infrastructure.persistence.repository;

import com.anthony.backend.application.mapper.AlbumMapper;
import com.anthony.backend.domain.model.Album;
import com.anthony.backend.domain.repository.AlbumRepository;
import com.anthony.backend.infrastructure.persistence.entity.AlbumEntity;
import com.anthony.backend.infrastructure.persistence.jpa.AlbumJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AlbumRepositoryImpl implements AlbumRepository {

    private final AlbumJpaRepository albumJpaRepository;
    private final AlbumMapper albumMapper;

    @Override
    public Album save(Album album) {
        AlbumEntity entity = albumMapper.toEntity(album);
        AlbumEntity savedEntity = albumJpaRepository.save(entity);
        return albumMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Album> findById(Long id) {
        return albumJpaRepository.findById(id)
                .map(albumMapper::toDomain);
    }

    @Override
    public List<Album> findAll() {
        return albumJpaRepository.findAll().stream()
                .map(albumMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Album> findByArtistId(Long artistId) {
        return albumJpaRepository.findByArtistId(artistId).stream()
                .map(albumMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Album> findByTitle(String title) {
        return albumJpaRepository.findByTitle(title)
                .map(albumMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        albumJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return albumJpaRepository.existsById(id);
    }
}

