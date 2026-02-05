package com.anthony.backend.application.service;

import com.anthony.backend.application.mapper.ArtistMapper;
import com.anthony.backend.domain.exception.DuplicateResourceExceptionHandler;
import com.anthony.backend.domain.exception.ResourceConflictExceptionHandler;
import com.anthony.backend.domain.exception.ResourceNotFoundExceptionHandler;
import com.anthony.backend.domain.model.Album;
import com.anthony.backend.domain.model.Artist;
import com.anthony.backend.domain.repository.ArtistRepository;
import com.anthony.backend.infrastructure.persistence.entity.AlbumEntity;
import com.anthony.backend.infrastructure.persistence.entity.ArtistEntity;
import com.anthony.backend.infrastructure.persistence.jpa.ArtistJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("ArtistService - Testes Unitários")
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ArtistJpaRepository artistJpaRepository;

    @Mock
    private ArtistMapper artistMapper;

    @InjectMocks
    private ArtistService artistService;

    private Artist artist;
    private ArtistEntity artistEntity;
    private Album album;

    @BeforeEach
    void setUp() {
        artist = Artist.builder()
                .id(1L)
                .name("Test Artist")
                .albums(new ArrayList<>())
                .build();

        artistEntity = new ArtistEntity();
        artistEntity.setId(1L);
        artistEntity.setName("Test Artist");

        album = Album.builder()
                .id(1L)
                .title("Test Album")
                .artist(artist)
                .build();
    }

    @Test
    @DisplayName("Deve criar artista com sucesso")
    void shouldCreateArtistSuccessfully() {
        String name = "New Artist";

        when(artistRepository.findByName(name)).thenReturn(Optional.empty());
        when(artistRepository.save(any(Artist.class))).thenAnswer(invocation -> {
            Artist savedArtist = invocation.getArgument(0);
            savedArtist.setId(2L);
            return savedArtist;
        });

        Artist result = artistService.create(name);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getId()).isEqualTo(2L);

        verify(artistRepository).findByName(name);
        verify(artistRepository).save(any(Artist.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar artista com nome duplicado")
    void shouldThrowExceptionWhenCreatingArtistWithDuplicateName() {
        String name = "Existing Artist";

        when(artistRepository.findByName(name)).thenReturn(Optional.of(artist));

        assertThatThrownBy(() -> artistService.create(name))
                .isInstanceOf(DuplicateResourceExceptionHandler.class)
                .hasMessageContaining("Artista")
                .hasMessageContaining("nome");

        verify(artistRepository).findByName(name);
        verify(artistRepository, never()).save(any(Artist.class));
    }

    @Test
    @DisplayName("Deve buscar artista por ID com sucesso")
    void shouldFindArtistByIdSuccessfully() {
        Long artistId = 1L;

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));

        Artist result = artistService.findById(artistId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(artistId);
        assertThat(result.getName()).isEqualTo("Test Artist");

        verify(artistRepository).findById(artistId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar artista inexistente")
    void shouldThrowExceptionWhenFindingNonExistentArtist() {
        Long artistId = 999L;

        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.findById(artistId))
                .isInstanceOf(ResourceNotFoundExceptionHandler.class)
                .hasMessageContaining("Artista");

        verify(artistRepository).findById(artistId);
    }

    @Test
    @DisplayName("Deve buscar todos os artistas sem filtros e sem álbuns")
    void shouldFindAllArtistsWithoutFiltersAndWithoutAlbums() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ArtistEntity> entities = Arrays.asList(artistEntity);
        Page<ArtistEntity> entityPage = new PageImpl<>(entities, pageable, 1);

        when(artistJpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(entityPage);
        when(artistMapper.toDomain(artistEntity)).thenReturn(artist);

        Page<Artist> result = artistService.findAll(null, null, false, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Artist");

        verify(artistJpaRepository).findAll(any(Specification.class), eq(pageable));
        verify(artistMapper).toDomain(artistEntity);
        verify(artistMapper, never()).toDomainWithAlbums(any());
    }

    @Test
    @DisplayName("Deve buscar todos os artistas sem filtros e com álbuns")
    void shouldFindAllArtistsWithoutFiltersAndWithAlbums() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ArtistEntity> entities = Arrays.asList(artistEntity);
        Page<ArtistEntity> entityPage = new PageImpl<>(entities, pageable, 1);

        when(artistJpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(entityPage);
        when(artistMapper.toDomainWithAlbums(artistEntity)).thenReturn(artist);

        Page<Artist> result = artistService.findAll(null, null, true, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Test Artist");

        verify(artistJpaRepository).findAll(any(Specification.class), eq(pageable));
        verify(artistMapper).toDomainWithAlbums(artistEntity);
        verify(artistMapper, never()).toDomain(any());
    }

    @Test
    @DisplayName("Deve buscar artistas filtrando por nome")
    void shouldFindArtistsFilteringByName() {
        Pageable pageable = PageRequest.of(0, 10);
        String name = "Test";
        List<ArtistEntity> entities = Arrays.asList(artistEntity);
        Page<ArtistEntity> entityPage = new PageImpl<>(entities, pageable, 1);

        when(artistJpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(entityPage);
        when(artistMapper.toDomain(artistEntity)).thenReturn(artist);

        Page<Artist> result = artistService.findAll(name, null, false, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(artistJpaRepository).findAll(any(Specification.class), eq(pageable));
        verify(artistMapper).toDomain(artistEntity);
    }

    @Test
    @DisplayName("Deve buscar artistas filtrando por título do álbum")
    void shouldFindArtistsFilteringByAlbumTitle() {
        Pageable pageable = PageRequest.of(0, 10);
        String albumTitle = "Test Album";
        List<ArtistEntity> entities = Arrays.asList(artistEntity);
        Page<ArtistEntity> entityPage = new PageImpl<>(entities, pageable, 1);

        when(artistJpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(entityPage);
        when(artistMapper.toDomain(artistEntity)).thenReturn(artist);

        Page<Artist> result = artistService.findAll(null, albumTitle, false, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(artistJpaRepository).findAll(any(Specification.class), eq(pageable));
        verify(artistMapper).toDomain(artistEntity);
    }

    @Test
    @DisplayName("Deve buscar artistas com múltiplos filtros")
    void shouldFindArtistsWithMultipleFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        String name = "Test";
        String albumTitle = "Test Album";
        List<ArtistEntity> entities = Arrays.asList(artistEntity);
        Page<ArtistEntity> entityPage = new PageImpl<>(entities, pageable, 1);

        when(artistJpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(entityPage);
        when(artistMapper.toDomainWithAlbums(artistEntity)).thenReturn(artist);

        Page<Artist> result = artistService.findAll(name, albumTitle, true, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(artistJpaRepository).findAll(any(Specification.class), eq(pageable));
        verify(artistMapper).toDomainWithAlbums(artistEntity);
    }

    @Test
    @DisplayName("Deve ignorar filtros vazios ou em branco")
    void shouldIgnoreEmptyOrBlankFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ArtistEntity> entities = Arrays.asList(artistEntity);
        Page<ArtistEntity> entityPage = new PageImpl<>(entities, pageable, 1);

        when(artistJpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(entityPage);
        when(artistMapper.toDomain(artistEntity)).thenReturn(artist);

        Page<Artist> result = artistService.findAll("", "   ", false, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(artistJpaRepository).findAll(any(Specification.class), eq(pageable));
        verify(artistMapper).toDomain(artistEntity);
    }

    @Test
    @DisplayName("Deve buscar página vazia quando não há artistas")
    void shouldReturnEmptyPageWhenNoArtistsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ArtistEntity> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(artistJpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(emptyPage);

        Page<Artist> result = artistService.findAll(null, null, false, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(artistJpaRepository).findAll(any(Specification.class), eq(pageable));
        verify(artistMapper, never()).toDomain(any());
        verify(artistMapper, never()).toDomainWithAlbums(any());
    }

    @Test
    @DisplayName("Deve atualizar artista com sucesso")
    void shouldUpdateArtistSuccessfully() {
        Long artistId = 1L;
        String newName = "Updated Artist";

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(artistRepository.save(artist)).thenReturn(artist);

        Artist result = artistService.update(artistId, newName);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(newName);

        verify(artistRepository).findById(artistId);
        verify(artistRepository).save(artist);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar artista inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentArtist() {
        Long artistId = 999L;
        String newName = "Updated Artist";

        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.update(artistId, newName))
                .isInstanceOf(ResourceNotFoundExceptionHandler.class)
                .hasMessageContaining("Artista");

        verify(artistRepository).findById(artistId);
        verify(artistRepository, never()).save(any(Artist.class));
    }

    @Test
    @DisplayName("Deve deletar artista sem álbuns com sucesso")
    void shouldDeleteArtistWithoutAlbumsSuccessfully() {
        Long artistId = 1L;

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        doNothing().when(artistRepository).deleteById(artistId);

        artistService.delete(artistId);

        verify(artistRepository).findById(artistId);
        verify(artistRepository).deleteById(artistId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar artista inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentArtist() {
        Long artistId = 999L;

        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.delete(artistId))
                .isInstanceOf(ResourceNotFoundExceptionHandler.class)
                .hasMessageContaining("Artista");

        verify(artistRepository).findById(artistId);
        verify(artistRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar artista com álbuns")
    void shouldThrowExceptionWhenDeletingArtistWithAlbums() {
        Long artistId = 1L;
        artist.getAlbums().add(album);

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));

        assertThatThrownBy(() -> artistService.delete(artistId))
                .isInstanceOf(ResourceConflictExceptionHandler.class)
                .hasMessageContaining("álbuns");

        verify(artistRepository).findById(artistId);
        verify(artistRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve criar artista com nome único e verificar unicidade")
    void shouldCreateArtistWithUniqueName() {
        String name = "Unique Artist";

        when(artistRepository.findByName(name)).thenReturn(Optional.empty());
        when(artistRepository.save(any(Artist.class))).thenAnswer(invocation -> {
            Artist savedArtist = invocation.getArgument(0);
            savedArtist.setId(3L);
            return savedArtist;
        });

        Artist result = artistService.create(name);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(name);

        verify(artistRepository).findByName(name);
        verify(artistRepository).save(argThat(a -> a.getName().equals(name)));
    }

    @Test
    @DisplayName("Deve buscar artistas com paginação correta")
    void shouldFindArtistsWithCorrectPagination() {
        Pageable pageable = PageRequest.of(1, 5);

        ArtistEntity entity1 = new ArtistEntity();
        entity1.setId(1L);
        entity1.setName("Artist 1");

        ArtistEntity entity2 = new ArtistEntity();
        entity2.setId(2L);
        entity2.setName("Artist 2");

        List<ArtistEntity> entities = Arrays.asList(entity1, entity2);
        Page<ArtistEntity> entityPage = new PageImpl<>(entities, pageable, 10);

        Artist artist1 = Artist.builder().id(1L).name("Artist 1").build();
        Artist artist2 = Artist.builder().id(2L).name("Artist 2").build();

        when(artistJpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(entityPage);
        when(artistMapper.toDomain(entity1)).thenReturn(artist1);
        when(artistMapper.toDomain(entity2)).thenReturn(artist2);

        Page<Artist> result = artistService.findAll(null, null, false, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getNumber()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(5);

        verify(artistJpaRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    @DisplayName("Deve atualizar apenas o nome do artista mantendo outros dados")
    void shouldUpdateOnlyArtistNameKeepingOtherData() {
        Long artistId = 1L;
        String newName = "Updated Name";
        artist.getAlbums().add(album);

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(artistRepository.save(artist)).thenReturn(artist);

        Artist result = artistService.update(artistId, newName);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo(newName);
        assertThat(result.getAlbums()).hasSize(1);
        assertThat(result.getId()).isEqualTo(artistId);

        verify(artistRepository).findById(artistId);
        verify(artistRepository).save(artist);
    }

    @Test
    @DisplayName("Deve deletar artista com lista de álbuns vazia")
    void shouldDeleteArtistWithEmptyAlbumsList() {
        Long artistId = 1L;
        artist.setAlbums(new ArrayList<>());

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        doNothing().when(artistRepository).deleteById(artistId);

        artistService.delete(artistId);

        assertThat(artist.getAlbums()).isEmpty();

        verify(artistRepository).findById(artistId);
        verify(artistRepository).deleteById(artistId);
    }
}

