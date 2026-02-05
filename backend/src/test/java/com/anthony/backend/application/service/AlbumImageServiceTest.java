package com.anthony.backend.application.service;

import com.anthony.backend.domain.exception.InvalidFileException;
import com.anthony.backend.domain.model.Album;
import com.anthony.backend.domain.model.AlbumImage;
import com.anthony.backend.domain.repository.AlbumImageRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AlbumImageService - Testes Unitários")
class AlbumImageServiceTest {

    @Mock
    private AlbumImageRepository albumImageRepository;

    @Mock
    private AlbumService albumService;

    @Mock
    private MinioStorageService minioStorageService;

    @InjectMocks
    private AlbumImageService albumImageService;

    private Album album;
    private AlbumImage albumImage;
    private MultipartFile validFile;

    @BeforeEach
    void setUp() {
        album = Album.builder()
                .id(1L)
                .title("Test Album")
                .build();

        albumImage = AlbumImage.builder()
                .id(1L)
                .fileKey("albums/1/image.jpg")
                .fileUrl("https://minio.example.com/presigned-url")
                .isDefault(false)
                .album(album)
                .build();

        validFile = mock(MultipartFile.class);
        when(validFile.getOriginalFilename()).thenReturn("test-image.jpg");
    }

    @Test
    @DisplayName("Deve fazer upload de imagens com sucesso")
    void shouldUploadImagesSuccessfully() {
        Long albumId = 1L;
        MultipartFile[] files = {validFile};
        String fileKey = "albums/1/test-image.jpg";
        String presignedUrl = "https://minio.example.com/presigned-url";

        when(albumService.findById(albumId)).thenReturn(album);
        when(minioStorageService.uploadFile(validFile, albumId)).thenReturn(fileKey);
        when(albumImageRepository.save(any(AlbumImage.class))).thenAnswer(invocation -> {
            AlbumImage image = invocation.getArgument(0);
            image.setId(1L);
            return image;
        });
        when(minioStorageService.getPresignedUrl(fileKey)).thenReturn(presignedUrl);

        List<AlbumImage> result = albumImageService.uploadImages(albumId, files, false);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFileKey()).isEqualTo(fileKey);
        assertThat(result.get(0).getFileUrl()).isEqualTo(presignedUrl);
        assertThat(result.get(0).getIsDefault()).isFalse();

        verify(albumService).findById(albumId);
        verify(minioStorageService).uploadFile(validFile, albumId);
        verify(albumImageRepository).save(any(AlbumImage.class));
        verify(minioStorageService).getPresignedUrl(fileKey);
        verify(albumImageRepository, never()).updateIsDefaultByAlbumId(anyLong(), anyBoolean());
    }

    @Test
    @DisplayName("Deve fazer upload de múltiplas imagens com sucesso")
    void shouldUploadMultipleImagesSuccessfully() {
        Long albumId = 1L;
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        when(file1.getOriginalFilename()).thenReturn("image1.jpg");
        when(file2.getOriginalFilename()).thenReturn("image2.png");

        MultipartFile[] files = {file1, file2};

        when(albumService.findById(albumId)).thenReturn(album);
        when(minioStorageService.uploadFile(any(MultipartFile.class), eq(albumId)))
                .thenReturn("albums/1/image1.jpg")
                .thenReturn("albums/1/image2.png");
        when(albumImageRepository.save(any(AlbumImage.class))).thenAnswer(invocation -> {
            AlbumImage image = invocation.getArgument(0);
            image.setId(1L);
            return image;
        });
        when(minioStorageService.getPresignedUrl(anyString())).thenReturn("https://minio.example.com/presigned-url");

        List<AlbumImage> result = albumImageService.uploadImages(albumId, files, false);

        assertThat(result).hasSize(2);
        verify(minioStorageService, times(2)).uploadFile(any(MultipartFile.class), eq(albumId));
        verify(albumImageRepository, times(2)).save(any(AlbumImage.class));
    }

    @Test
    @DisplayName("Deve definir primeira imagem como padrão quando setAsDefault é true")
    void shouldSetFirstImageAsDefaultWhenSetAsDefaultIsTrue() {
        Long albumId = 1L;
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        when(file1.getOriginalFilename()).thenReturn("image1.jpg");
        when(file2.getOriginalFilename()).thenReturn("image2.jpg");

        MultipartFile[] files = {file1, file2};

        when(albumService.findById(albumId)).thenReturn(album);
        when(minioStorageService.uploadFile(any(MultipartFile.class), eq(albumId)))
                .thenReturn("albums/1/image1.jpg")
                .thenReturn("albums/1/image2.jpg");
        when(albumImageRepository.save(any(AlbumImage.class))).thenAnswer(invocation -> {
            AlbumImage image = invocation.getArgument(0);
            image.setId(1L);
            return image;
        });
        when(minioStorageService.getPresignedUrl(anyString())).thenReturn("https://minio.example.com/presigned-url");

        List<AlbumImage> result = albumImageService.uploadImages(albumId, files, true);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getIsDefault()).isTrue();
        assertThat(result.get(1).getIsDefault()).isFalse();
        verify(albumImageRepository).updateIsDefaultByAlbumId(albumId, false);
    }

    @Test
    @DisplayName("Deve lançar exceção quando arquivo tem extensão inválida")
    void shouldThrowExceptionWhenFileHasInvalidExtension() {
        Long albumId = 1L;
        MultipartFile invalidFile = mock(MultipartFile.class);
        when(invalidFile.getOriginalFilename()).thenReturn("document.pdf");
        MultipartFile[] files = {invalidFile};

        when(albumService.findById(albumId)).thenReturn(album);

        assertThatThrownBy(() -> albumImageService.uploadImages(albumId, files, false))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("Extensão de arquivo 'pdf' não permitida");

        verify(minioStorageService, never()).uploadFile(any(), anyLong());
        verify(albumImageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome do arquivo é vazio")
    void shouldThrowExceptionWhenFilenameIsEmpty() {
        Long albumId = 1L;
        MultipartFile emptyNameFile = mock(MultipartFile.class);
        when(emptyNameFile.getOriginalFilename()).thenReturn("");
        MultipartFile[] files = {emptyNameFile};

        when(albumService.findById(albumId)).thenReturn(album);

        assertThatThrownBy(() -> albumImageService.uploadImages(albumId, files, false))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("Nome do arquivo não pode ser vazio");

        verify(minioStorageService, never()).uploadFile(any(), anyLong());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nome do arquivo é nulo")
    void shouldThrowExceptionWhenFilenameIsNull() {
        Long albumId = 1L;
        MultipartFile nullNameFile = mock(MultipartFile.class);
        when(nullNameFile.getOriginalFilename()).thenReturn(null);
        MultipartFile[] files = {nullNameFile};

        when(albumService.findById(albumId)).thenReturn(album);

        assertThatThrownBy(() -> albumImageService.uploadImages(albumId, files, false))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("Nome do arquivo não pode ser vazio");
    }

    @Test
    @DisplayName("Deve aceitar arquivo com extensão JPG em maiúscula")
    void shouldAcceptFileWithUppercaseJPGExtension() {
        Long albumId = 1L;
        MultipartFile upperCaseFile = mock(MultipartFile.class);
        when(upperCaseFile.getOriginalFilename()).thenReturn("IMAGE.JPG");
        MultipartFile[] files = {upperCaseFile};

        when(albumService.findById(albumId)).thenReturn(album);
        when(minioStorageService.uploadFile(any(), anyLong())).thenReturn("albums/1/image.jpg");
        when(albumImageRepository.save(any())).thenAnswer(invocation -> {
            AlbumImage image = invocation.getArgument(0);
            image.setId(1L);
            return image;
        });
        when(minioStorageService.getPresignedUrl(anyString())).thenReturn("https://minio.example.com/presigned-url");

        List<AlbumImage> result = albumImageService.uploadImages(albumId, files, false);

        assertThat(result).hasSize(1);
        verify(minioStorageService).uploadFile(upperCaseFile, albumId);
    }

    @Test
    @DisplayName("Deve buscar imagens do álbum com URLs pré-assinadas")
    void shouldGetAlbumImagesWithPresignedUrls() {
        Long albumId = 1L;
        AlbumImage image1 = AlbumImage.builder()
                .id(1L)
                .fileKey("albums/1/image1.jpg")
                .album(album)
                .build();
        AlbumImage image2 = AlbumImage.builder()
                .id(2L)
                .fileKey("albums/1/image2.jpg")
                .album(album)
                .build();

        when(albumImageRepository.findByAlbumId(albumId)).thenReturn(Arrays.asList(image1, image2));
        when(minioStorageService.getPresignedUrl("albums/1/image1.jpg")).thenReturn("https://minio.example.com/url1");
        when(minioStorageService.getPresignedUrl("albums/1/image2.jpg")).thenReturn("https://minio.example.com/url2");

        List<AlbumImage> result = albumImageService.getAlbumImages(albumId);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getFileUrl()).isEqualTo("https://minio.example.com/url1");
        assertThat(result.get(1).getFileUrl()).isEqualTo("https://minio.example.com/url2");
        verify(albumImageRepository).findByAlbumId(albumId);
        verify(minioStorageService, times(2)).getPresignedUrl(anyString());
    }

    @Test
    @DisplayName("Deve buscar imagem por ID com URL pré-assinada")
    void shouldGetImageByIdWithPresignedUrl() {
        Long imageId = 1L;
        String presignedUrl = "https://minio.example.com/presigned-url";

        when(albumImageRepository.findById(imageId)).thenReturn(Optional.of(albumImage));
        when(minioStorageService.getPresignedUrl(albumImage.getFileKey())).thenReturn(presignedUrl);

        AlbumImage result = albumImageService.getImageById(imageId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(imageId);
        assertThat(result.getFileUrl()).isEqualTo(presignedUrl);
        verify(albumImageRepository).findById(imageId);
        verify(minioStorageService).getPresignedUrl(albumImage.getFileKey());
    }

    @Test
    @DisplayName("Deve lançar exceção quando imagem não é encontrada por ID")
    void shouldThrowExceptionWhenImageNotFoundById() {
        // Arrange
        Long imageId = 999L;
        when(albumImageRepository.findById(imageId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumImageService.getImageById(imageId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Imagem não encontrada");

        verify(albumImageRepository).findById(imageId);
        verify(minioStorageService, never()).getPresignedUrl(anyString());
    }

    @Test
    @DisplayName("Deve deletar imagem com sucesso")
    void shouldDeleteImageSuccessfully() {
        Long imageId = 1L;
        when(albumImageRepository.findById(imageId)).thenReturn(Optional.of(albumImage));
        doNothing().when(minioStorageService).deleteFile(albumImage.getFileKey());
        doNothing().when(albumImageRepository).deleteById(imageId);

        albumImageService.deleteImage(imageId);

        verify(albumImageRepository).findById(imageId);
        verify(minioStorageService).deleteFile(albumImage.getFileKey());
        verify(albumImageRepository).deleteById(imageId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar imagem não encontrada")
    void shouldThrowExceptionWhenDeletingNonExistentImage() {
        Long imageId = 999L;
        when(albumImageRepository.findById(imageId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumImageService.deleteImage(imageId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Imagem não encontrada");

        verify(albumImageRepository).findById(imageId);
        verify(minioStorageService, never()).deleteFile(anyString());
        verify(albumImageRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve definir imagem como padrão")
    void shouldSetImageAsDefault() {
        Long albumId = 1L;
        Long imageId = 1L;
        String presignedUrl = "https://minio.example.com/presigned-url";

        when(albumImageRepository.findById(imageId)).thenReturn(Optional.of(albumImage));
        when(albumImageRepository.save(albumImage)).thenReturn(albumImage);
        when(minioStorageService.getPresignedUrl(albumImage.getFileKey())).thenReturn(presignedUrl);
        doNothing().when(albumImageRepository).updateIsDefaultByAlbumId(albumId, false);

        AlbumImage result = albumImageService.setAsDefault(albumId, imageId);

        assertThat(result).isNotNull();
        assertThat(result.getIsDefault()).isTrue();
        assertThat(result.getFileUrl()).isEqualTo(presignedUrl);
        verify(albumImageRepository).updateIsDefaultByAlbumId(albumId, false);
        verify(albumImageRepository).findById(imageId);
        verify(albumImageRepository).save(albumImage);
        verify(minioStorageService).getPresignedUrl(albumImage.getFileKey());
    }

    @Test
    @DisplayName("Deve lançar exceção ao definir imagem não encontrada como padrão")
    void shouldThrowExceptionWhenSettingNonExistentImageAsDefault() {
        Long albumId = 1L;
        Long imageId = 999L;

        doNothing().when(albumImageRepository).updateIsDefaultByAlbumId(albumId, false);
        when(albumImageRepository.findById(imageId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumImageService.setAsDefault(albumId, imageId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Imagem não encontrada");

        verify(albumImageRepository).updateIsDefaultByAlbumId(albumId, false);
        verify(albumImageRepository).findById(imageId);
        verify(albumImageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando imagem não pertence ao álbum")
    void shouldThrowExceptionWhenImageDoesNotBelongToAlbum() {
        Long albumId = 2L;
        Long imageId = 1L;
        Album differentAlbum = Album.builder().id(1L).build();
        albumImage.setAlbum(differentAlbum);

        doNothing().when(albumImageRepository).updateIsDefaultByAlbumId(albumId, false);
        when(albumImageRepository.findById(imageId)).thenReturn(Optional.of(albumImage));

        assertThatThrownBy(() -> albumImageService.setAsDefault(albumId, imageId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Imagem não pertence a este álbum");

        verify(albumImageRepository).updateIsDefaultByAlbumId(albumId, false);
        verify(albumImageRepository).findById(imageId);
        verify(albumImageRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve aceitar extensões jpeg, jpg e png")
    void shouldAcceptValidExtensions() {
        Long albumId = 1L;
        MultipartFile jpegFile = mock(MultipartFile.class);
        MultipartFile jpgFile = mock(MultipartFile.class);
        MultipartFile pngFile = mock(MultipartFile.class);

        when(jpegFile.getOriginalFilename()).thenReturn("image.jpeg");
        when(jpgFile.getOriginalFilename()).thenReturn("image.jpg");
        when(pngFile.getOriginalFilename()).thenReturn("image.png");

        MultipartFile[] files = {jpegFile, jpgFile, pngFile};

        when(albumService.findById(albumId)).thenReturn(album);
        when(minioStorageService.uploadFile(any(), anyLong()))
                .thenReturn("albums/1/image1.jpeg")
                .thenReturn("albums/1/image2.jpg")
                .thenReturn("albums/1/image3.png");
        when(albumImageRepository.save(any())).thenAnswer(invocation -> {
            AlbumImage image = invocation.getArgument(0);
            image.setId(1L);
            return image;
        });
        when(minioStorageService.getPresignedUrl(anyString())).thenReturn("https://minio.example.com/presigned-url");

        List<AlbumImage> result = albumImageService.uploadImages(albumId, files, false);

        assertThat(result).hasSize(3);
        verify(minioStorageService, times(3)).uploadFile(any(), eq(albumId));
    }

    @Test
    @DisplayName("Deve lançar exceção para arquivo sem extensão")
    void shouldThrowExceptionForFileWithoutExtension() {
        Long albumId = 1L;
        MultipartFile noExtensionFile = mock(MultipartFile.class);
        when(noExtensionFile.getOriginalFilename()).thenReturn("imagefile");
        MultipartFile[] files = {noExtensionFile};

        when(albumService.findById(albumId)).thenReturn(album);

        assertThatThrownBy(() -> albumImageService.uploadImages(albumId, files, false))
                .isInstanceOf(InvalidFileException.class)
                .hasMessageContaining("não permitida");

        verify(minioStorageService, never()).uploadFile(any(), anyLong());
    }
}
