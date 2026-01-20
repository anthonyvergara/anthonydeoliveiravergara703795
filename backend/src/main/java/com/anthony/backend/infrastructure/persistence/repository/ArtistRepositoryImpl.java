package com.anthony.backend.infrastructure.persistence.repository;

import com.anthony.backend.application.mapper.ArtistMapper;
import com.anthony.backend.domain.model.Artist;
import com.anthony.backend.domain.repository.ArtistRepository;
import com.anthony.backend.infrastructure.persistence.entity.ArtistEntity;
import com.anthony.backend.infrastructure.persistence.jpa.ArtistJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ArtistRepositoryImpl implements ArtistRepository {

    private final ArtistJpaRepository artistJpaRepository;
    private final ArtistMapper artistMapper;

    @Override
    public Artist save(Artist artist) {
        ArtistEntity entity = artistMapper.toEntity(artist);
        ArtistEntity savedEntity = artistJpaRepository.save(entity);
        return artistMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Artist> findById(Long id) {
        return artistJpaRepository.findById(id)
                .map(artistMapper::toDomain);
    }

    @Override
    public List<Artist> findAll() {
        return artistJpaRepository.findAll().stream()
                .map(artistMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Artist> findByName(String name) {
        return artistJpaRepository.findByName(name)
                .map(artistMapper::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        artistJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return artistJpaRepository.existsById(id);
    }
}

