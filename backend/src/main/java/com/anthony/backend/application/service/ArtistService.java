package com.anthony.backend.application.service;

import com.anthony.backend.application.mapper.ArtistMapper;
import com.anthony.backend.domain.exception.DuplicateResourceExceptionHandler;
import com.anthony.backend.domain.exception.ResourceConflictExceptionHandler;
import com.anthony.backend.domain.exception.ResourceNotFoundExceptionHandler;
import com.anthony.backend.domain.model.Artist;
import com.anthony.backend.domain.repository.ArtistRepository;
import com.anthony.backend.infrastructure.persistence.entity.ArtistEntity;
import com.anthony.backend.infrastructure.persistence.jpa.ArtistJpaRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistJpaRepository artistJpaRepository;
    private final ArtistMapper artistMapper;

    public ArtistService(ArtistRepository artistRepository,
                         ArtistJpaRepository artistJpaRepository,
                         ArtistMapper artistMapper) {
        this.artistRepository = artistRepository;
        this.artistJpaRepository = artistJpaRepository;
        this.artistMapper = artistMapper;
    }

    @Transactional
    public Artist create(String name) {
        artistRepository.findByName(name).ifPresent(artist -> {
            throw new DuplicateResourceExceptionHandler("Artista", "nome", name);
        });
        Artist artist = Artist.builder()
                .name(name)
                .build();

        return artistRepository.save(artist);
    }

    public Artist findById(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExceptionHandler("Artista", id));
    }

    public Page<Artist> findAll(String name, String albumTitle, boolean includeAlbums, Pageable pageable) {
        Specification<ArtistEntity> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase() + "%"
                ));
            }

            if (albumTitle != null && !albumTitle.isBlank()) {
                Join<Object, Object> albumJoin = root.join("albums");
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(albumJoin.get("title")),
                        "%" + albumTitle.toLowerCase() + "%"
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return artistJpaRepository.findAll(spec, pageable)
                .map(entity -> includeAlbums ? artistMapper.toDomainWithAlbums(entity) : artistMapper.toDomain(entity));
    }

    @Transactional
    public Artist update(Long id, String name) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExceptionHandler("Artista", id));

        artist.setName(name);

        return artistRepository.save(artist);
    }

    @Transactional
    public void delete(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundExceptionHandler("Artista", id));

        if (!artist.getAlbums().isEmpty()) {
            throw new ResourceConflictExceptionHandler("Não é possível deletar artista que possui álbuns");
        }

        artistRepository.deleteById(id);
    }
}
