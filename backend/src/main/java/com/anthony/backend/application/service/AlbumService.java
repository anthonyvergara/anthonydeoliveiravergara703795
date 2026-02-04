package com.anthony.backend.application.service;

import com.anthony.backend.application.mapper.AlbumMapper;
import com.anthony.backend.domain.model.Album;
import com.anthony.backend.domain.model.Artist;
import com.anthony.backend.domain.repository.AlbumRepository;
import com.anthony.backend.domain.repository.ArtistRepository;
import com.anthony.backend.infrastructure.persistence.entity.AlbumEntity;
import com.anthony.backend.infrastructure.persistence.jpa.AlbumJpaRepository;
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
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final AlbumJpaRepository albumJpaRepository;
    private final AlbumMapper albumMapper;

    public AlbumService(AlbumRepository albumRepository,
                        ArtistRepository artistRepository,
                        AlbumJpaRepository albumJpaRepository,
                        AlbumMapper albumMapper) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.albumJpaRepository = albumJpaRepository;
        this.albumMapper = albumMapper;
    }

    @Transactional
    public Album create(String title, Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artista não encontrado"));

        Album album = Album.builder()
                .title(title)
                .build();

        artist.addAlbum(album);

        return albumRepository.save(album);
    }

    public Album findById(Long id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Álbum não encontrado"));
    }

    public Page<Album> findAll(String title, String artistName, Long artistId, Pageable pageable) {
        Specification<AlbumEntity> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (title != null && !title.isBlank()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("title")),
                        "%" + title.toLowerCase() + "%"
                ));
            }

            if (artistName != null && !artistName.isBlank()) {
                Join<Object, Object> artistJoin = root.join("artist");
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(artistJoin.get("name")),
                        "%" + artistName.toLowerCase() + "%"
                ));
            }

            if (artistId != null) {
                Join<Object, Object> artistJoin = root.join("artist");
                predicates.add(criteriaBuilder.equal(artistJoin.get("id"), artistId));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return albumJpaRepository.findAll(spec, pageable)
                .map(albumMapper::toDomain);
    }

    @Transactional
    public Album update(Long id, String title, Long artistId) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Álbum não encontrado"));

        Artist oldArtist = album.getArtist();
        Artist newArtist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Artista não encontrado"));

        if (oldArtist != null && !oldArtist.getId().equals(newArtist.getId())) {
            oldArtist.removeAlbum(album);
        }

        album.setTitle(title);

        if (oldArtist == null || !oldArtist.getId().equals(newArtist.getId())) {
            newArtist.addAlbum(album);
        }

        return albumRepository.save(album);
    }

    @Transactional
    public void delete(Long id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Álbum não encontrado"));

        Artist artist = album.getArtist();
        if (artist != null) {
            artist.removeAlbum(album);
        }

        albumRepository.deleteById(id);
    }
}
