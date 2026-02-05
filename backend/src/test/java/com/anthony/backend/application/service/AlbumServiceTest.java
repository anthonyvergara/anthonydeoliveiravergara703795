package com.anthony.backend.application.service;

import com.anthony.backend.application.mapper.AlbumMapper;
import com.anthony.backend.domain.exception.ResourceNotFoundExceptionHandler;
import com.anthony.backend.domain.model.Album;
import com.anthony.backend.domain.model.AlbumImage;
import com.anthony.backend.domain.model.Artist;
import com.anthony.backend.domain.repository.AlbumRepository;
import com.anthony.backend.domain.repository.ArtistRepository;
import com.anthony.backend.infrastructure.persistence.entity.AlbumEntity;
import com.anthony.backend.infrastructure.persistence.entity.ArtistEntity;
import com.anthony.backend.infrastructure.persistence.jpa.AlbumJpaRepository;
import com.anthony.backend.infrastructure.storage.MinioStorageService;
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
@DisplayName("AlbumService - Testes Unitários")
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private AlbumJpaRepository albumJpaRepository;

    @Mock
    private AlbumMapper albumMapper;

    @Mock
    private MinioStorageService minioStorageService;

    @InjectMocks
    private AlbumService albumService;

    private Artist artist;
    private Album album;
    private AlbumEntity albumEntity;
    private ArtistEntity artistEntity;

    @BeforeEach
    void setUp() {
        artist = Artist.builder()
                .id(1L)
                .name("Test Artist")
                .albums(new ArrayList<>())
                .build();

        album = Album.builder()
                .id(1L)
                .title("Test Album")
                .artist(artist)
                .images(new ArrayList<>())
                .build();

        artistEntity = new ArtistEntity();
        artistEntity.setId(1L);
        artistEntity.setName("Test Artist");

        albumEntity = new AlbumEntity();
        albumEntity.setId(1L);
        albumEntity.setTitle("Test Album");
        albumEntity.setArtist(artistEntity);
    }

    @Test
    @DisplayName("Deve criar álbum com sucesso")
    void shouldCreateAlbumSuccessfully() {
        String title = "New Album";
        Long artistId = 1L;

        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(albumRepository.save(any(Album.class))).thenAnswer(invocation -> {
            Album savedAlbum = invocation.getArgument(0);
            savedAlbum.setId(2L);
            return savedAlbum;
        });

        Album result = albumService.create(title, artistId);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getArtist()).isEqualTo(artist);
        assertThat(artist.getAlbums()).contains(result);

        verify(artistRepository).findById(artistId);
        verify(albumRepository).save(any(Album.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar álbum com artista inexistente")
    void shouldThrowExceptionWhenCreatingAlbumWithNonExistentArtist() {
        String title = "New Album";
        Long artistId = 999L;

        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.create(title, artistId))
                .isInstanceOf(ResourceNotFoundExceptionHandler.class)
                .hasMessageContaining("Artista");

        verify(artistRepository).findById(artistId);
        verify(albumRepository, never()).save(any(Album.class));
    }

    @Test
    @DisplayName("Deve buscar álbum por ID com sucesso")
    void shouldFindAlbumByIdSuccessfully() {
        Long albumId = 1L;
        AlbumImage defaultImage = AlbumImage.builder()
                .id(1L)
                .fileKey("albums/1/image.jpg")
                .isDefault(true)
                .album(album)
                .build();
        album.getImages().add(defaultImage);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(minioStorageService.getPresignedUrl("albums/1/image.jpg"))
                .thenReturn("https://minio.example.com/presigned-url");

        Album result = albumService.findById(albumId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(albumId);
        assertThat(result.getImages().get(0).getFileUrl()).isEqualTo("https://minio.example.com/presigned-url");

        verify(albumRepository).findById(albumId);
        verify(minioStorageService).getPresignedUrl("albums/1/image.jpg");
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar álbum inexistente")
    void shouldThrowExceptionWhenFindingNonExistentAlbum() {
        Long albumId = 999L;

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.findById(albumId))
                .isInstanceOf(ResourceNotFoundExceptionHandler.class)
                .hasMessageContaining("Álbum");

        verify(albumRepository).findById(albumId);
        verify(minioStorageService, never()).getPresignedUrl(anyString());
    }

    @Test
    @DisplayName("Deve buscar álbum por ID sem imagens")
    void shouldFindAlbumByIdWithoutImages() {
        Long albumId = 1L;

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        Album result = albumService.findById(albumId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(albumId);
        assertThat(result.getImages()).isEmpty();

        verify(albumRepository).findById(albumId);
        verify(minioStorageService, never()).getPresignedUrl(anyString());
    }

    @Test
    @DisplayName("Deve buscar todos os álbuns sem filtros")
    void shouldFindAllAlbumsWithoutFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        List<AlbumEntity> entities = Arrays.asList(albumEntity);
        Page<AlbumEntity> entityPage = new PageImpl<>(entities, pageable, 1);

        when(albumJpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(entityPage);
        when(albumMapper.toDomain(albumEntity)).thenReturn(album);

        Page<Album> result = albumService.findAll(null, null, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Album");

        verify(albumJpaRepository).findAll(any(Specification.class), eq(pageable));
        verify(albumMapper).toDomain(albumEntity);
    }

    @Test
    @DisplayName("Deve buscar álbuns filtrando por título")
    void shouldFindAlbumsFilteringByTitle() {
        Pageable pageable = PageRequest.of(0, 10);
        String title = "Test";
        List<AlbumEntity> entities = Arrays.asList(albumEntity);
        Page<AlbumEntity> entityPage = new PageImpl<>(entities, pageable, 1);

        when(albumJpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(entityPage);
        when(albumMapper.toDomain(albumEntity)).thenReturn(album);

        Page<Album> result = albumService.findAll(title, null, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(albumJpaRepository).findAll(any(Specification.class), eq(pageable));
        verify(albumMapper).toDomain(albumEntity);
    }

    @Test
    @DisplayName("Deve buscar álbuns filtrando por nome do artista")
    void shouldFindAlbumsFilteringByArtistName() {
        Pageable pageable = PageRequest.of(0, 10);
        String artistName = "Test Artist";
        List<AlbumEntity> entities = Arrays.asList(albumEntity);
        Page<AlbumEntity> entityPage = new PageImpl<>(entities, pageable, 1);

        when(albumJpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(entityPage);
        when(albumMapper.toDomain(albumEntity)).thenReturn(album);

        Page<Album> result = albumService.findAll(null, artistName, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(albumJpaRepository).findAll(any(Specification.class), eq(pageable));
        verify(albumMapper).toDomain(albumEntity);
    }

    @Test
    @DisplayName("Deve buscar álbuns filtrando por ID do artista")
    void shouldFindAlbumsFilteringByArtistId() {
        Pageable pageable = PageRequest.of(0, 10);
        Long artistId = 1L;
        List<AlbumEntity> entities = Arrays.asList(albumEntity);
        Page<AlbumEntity> entityPage = new PageImpl<>(entities, pageable, 1);

        when(albumJpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(entityPage);
        when(albumMapper.toDomain(albumEntity)).thenReturn(album);

        Page<Album> result = albumService.findAll(null, null, artistId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(albumJpaRepository).findAll(any(Specification.class), eq(pageable));
        verify(albumMapper).toDomain(albumEntity);
    }

    @Test
    @DisplayName("Deve buscar álbuns com múltiplos filtros")
    void shouldFindAlbumsWithMultipleFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        String title = "Test";
        String artistName = "Test Artist";
        Long artistId = 1L;
        List<AlbumEntity> entities = Arrays.asList(albumEntity);
        Page<AlbumEntity> entityPage = new PageImpl<>(entities, pageable, 1);

        when(albumJpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(entityPage);
        when(albumMapper.toDomain(albumEntity)).thenReturn(album);

        Page<Album> result = albumService.findAll(title, artistName, artistId, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);

        verify(albumJpaRepository).findAll(any(Specification.class), eq(pageable));
        verify(albumMapper).toDomain(albumEntity);
    }

    @Test
    @DisplayName("Deve popular URLs das imagens padrão ao buscar todos os álbuns")
    void shouldPopulateDefaultImageUrlsWhenFindingAllAlbums() {
        Pageable pageable = PageRequest.of(0, 10);
        AlbumImage defaultImage = AlbumImage.builder()
                .id(1L)
                .fileKey("albums/1/image.jpg")
                .isDefault(true)
                .build();
        album.getImages().add(defaultImage);

        List<AlbumEntity> entities = Arrays.asList(albumEntity);
        Page<AlbumEntity> entityPage = new PageImpl<>(entities, pageable, 1);

        when(albumJpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(entityPage);
        when(albumMapper.toDomain(albumEntity)).thenReturn(album);
        when(minioStorageService.getPresignedUrl("albums/1/image.jpg"))
                .thenReturn("https://minio.example.com/presigned-url");

        Page<Album> result = albumService.findAll(null, null, null, pageable);

        assertThat(result.getContent().get(0).getImages().get(0).getFileUrl())
                .isEqualTo("https://minio.example.com/presigned-url");

        verify(minioStorageService).getPresignedUrl("albums/1/image.jpg");
    }

    @Test
    @DisplayName("Deve atualizar álbum sem trocar de artista")
    void shouldUpdateAlbumWithoutChangingArtist() {
        Long albumId = 1L;
        String newTitle = "Updated Album";
        Long artistId = 1L;

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(albumRepository.save(album)).thenReturn(album);

        Album result = albumService.update(albumId, newTitle, artistId);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(newTitle);
        assertThat(result.getArtist()).isEqualTo(artist);

        verify(albumRepository).findById(albumId);
        verify(artistRepository).findById(artistId);
        verify(albumRepository).save(album);
    }

    @Test
    @DisplayName("Deve atualizar álbum trocando de artista")
    void shouldUpdateAlbumChangingArtist() {
        Long albumId = 1L;
        String newTitle = "Updated Album";
        Long newArtistId = 2L;

        Artist newArtist = Artist.builder()
                .id(2L)
                .name("New Artist")
                .albums(new ArrayList<>())
                .build();

        artist.getAlbums().add(album);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(artistRepository.findById(newArtistId)).thenReturn(Optional.of(newArtist));
        when(albumRepository.save(album)).thenReturn(album);

        Album result = albumService.update(albumId, newTitle, newArtistId);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(newTitle);
        assertThat(result.getArtist()).isEqualTo(newArtist);
        assertThat(artist.getAlbums()).doesNotContain(album);
        assertThat(newArtist.getAlbums()).contains(album);

        verify(albumRepository).findById(albumId);
        verify(artistRepository).findById(newArtistId);
        verify(albumRepository).save(album);
    }

    @Test
    @DisplayName("Deve atualizar álbum adicionando artista quando não tinha")
    void shouldUpdateAlbumAddingArtistWhenDidNotHaveOne() {
        Long albumId = 1L;
        String newTitle = "Updated Album";
        Long artistId = 1L;

        album.setArtist(null);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(albumRepository.save(album)).thenReturn(album);

        Album result = albumService.update(albumId, newTitle, artistId);

        assertThat(result).isNotNull();
        assertThat(result.getArtist()).isEqualTo(artist);
        assertThat(artist.getAlbums()).contains(album);

        verify(albumRepository).findById(albumId);
        verify(artistRepository).findById(artistId);
        verify(albumRepository).save(album);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar álbum inexistente")
    void shouldThrowExceptionWhenUpdatingNonExistentAlbum() {
        Long albumId = 999L;
        String newTitle = "Updated Album";
        Long artistId = 1L;

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.update(albumId, newTitle, artistId))
                .isInstanceOf(ResourceNotFoundExceptionHandler.class)
                .hasMessageContaining("Álbum");

        verify(albumRepository).findById(albumId);
        verify(artistRepository, never()).findById(anyLong());
        verify(albumRepository, never()).save(any(Album.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar álbum com artista inexistente")
    void shouldThrowExceptionWhenUpdatingAlbumWithNonExistentArtist() {
        Long albumId = 1L;
        String newTitle = "Updated Album";
        Long artistId = 999L;

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(artistRepository.findById(artistId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.update(albumId, newTitle, artistId))
                .isInstanceOf(ResourceNotFoundExceptionHandler.class)
                .hasMessageContaining("Artista");

        verify(albumRepository).findById(albumId);
        verify(artistRepository).findById(artistId);
        verify(albumRepository, never()).save(any(Album.class));
    }

    @Test
    @DisplayName("Deve deletar álbum com sucesso")
    void shouldDeleteAlbumSuccessfully() {
        Long albumId = 1L;
        artist.getAlbums().add(album);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        doNothing().when(albumRepository).deleteById(albumId);

        albumService.delete(albumId);

        assertThat(artist.getAlbums()).doesNotContain(album);

        verify(albumRepository).findById(albumId);
        verify(albumRepository).deleteById(albumId);
    }

    @Test
    @DisplayName("Deve deletar álbum sem artista")
    void shouldDeleteAlbumWithoutArtist() {
        Long albumId = 1L;
        album.setArtist(null);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        doNothing().when(albumRepository).deleteById(albumId);

        albumService.delete(albumId);

        verify(albumRepository).findById(albumId);
        verify(albumRepository).deleteById(albumId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar álbum inexistente")
    void shouldThrowExceptionWhenDeletingNonExistentAlbum() {
        Long albumId = 999L;

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.delete(albumId))
                .isInstanceOf(ResourceNotFoundExceptionHandler.class)
                .hasMessageContaining("Álbum");

        verify(albumRepository).findById(albumId);
        verify(albumRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Não deve popular URL de imagens não padrão")
    void shouldNotPopulateNonDefaultImageUrls() {
        Long albumId = 1L;
        AlbumImage nonDefaultImage = AlbumImage.builder()
                .id(1L)
                .fileKey("albums/1/image.jpg")
                .isDefault(false)
                .album(album)
                .build();
        album.getImages().add(nonDefaultImage);

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        Album result = albumService.findById(albumId);

        assertThat(result.getImages().get(0).getFileUrl()).isNull();
        verify(minioStorageService, never()).getPresignedUrl(anyString());
    }

    @Test
    @DisplayName("Deve buscar página vazia quando não há álbuns")
    void shouldReturnEmptyPageWhenNoAlbumsExist() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<AlbumEntity> emptyPage = new PageImpl<>(new ArrayList<>(), pageable, 0);

        when(albumJpaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(emptyPage);

        Page<Album> result = albumService.findAll(null, null, null, pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();

        verify(albumJpaRepository).findAll(any(Specification.class), eq(pageable));
        verify(albumMapper, never()).toDomain(any());
    }
}

