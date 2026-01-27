package com.anthony.backend.application.service;

import com.anthony.backend.application.mapper.AlbumImageMapper;
import com.anthony.backend.domain.model.Album;
import com.anthony.backend.domain.model.AlbumImage;
import com.anthony.backend.domain.repository.AlbumImageRepository;
import com.anthony.backend.infrastructure.storage.MinioStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlbumImageService {

    private final AlbumImageRepository albumImageRepository;
    private final AlbumService albumService;
    private final MinioStorageService minioStorageService;
    private final AlbumImageMapper albumImageMapper;

    @Transactional
    public List<AlbumImage> uploadImages(Long albumId, MultipartFile[] files, Boolean setAsDefault) {
        Album album = albumService.findById(albumId);
        List<AlbumImage> uploadedImages = new ArrayList<>();

        if (Boolean.TRUE.equals(setAsDefault)) {
            List<AlbumImage> existingImages = albumImageRepository.findByAlbumId(albumId);
            existingImages.forEach(img -> {
                img.setIsDefault(false);
                albumImageRepository.save(img);
            });
        }

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];

            String fileName = minioStorageService.uploadFile(file, albumId);

            boolean isDefault = Boolean.TRUE.equals(setAsDefault) && i == 0;

            AlbumImage albumImage = AlbumImage.builder()
                    .fileName(fileName)
                    .fileUrl(fileName) // Armazena o nome do arquivo, a URL será gerada dinamicamente
                    .isDefault(isDefault)
                    .album(album)
                    .build();

            AlbumImage savedImage = albumImageRepository.save(albumImage);

            String presignedUrl = minioStorageService.getPresignedUrl(savedImage.getFileName());
            savedImage.setFileUrl(presignedUrl);

            uploadedImages.add(savedImage);
        }

        return uploadedImages;
    }

    public List<AlbumImage> getAlbumImages(Long albumId) {
        List<AlbumImage> images = albumImageRepository.findByAlbumId(albumId);

        images.forEach(image -> {
            String presignedUrl = minioStorageService.getPresignedUrl(image.getFileName());
            image.setFileUrl(presignedUrl);
        });

        return images;
    }

    public AlbumImage getImageById(Long id) {
        AlbumImage image = albumImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagem não encontrada"));

        String presignedUrl = minioStorageService.getPresignedUrl(image.getFileName());
        image.setFileUrl(presignedUrl);

        return image;
    }

    @Transactional
    public void deleteImage(Long id) {
        AlbumImage image = albumImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Imagem não encontrada"));

        minioStorageService.deleteFile(image.getFileName());

        albumImageRepository.deleteById(id);
    }

    @Transactional
    public AlbumImage setAsDefault(Long albumId, Long imageId) {
        List<AlbumImage> images = albumImageRepository.findByAlbumId(albumId);
        images.forEach(img -> {
            img.setIsDefault(false);
            albumImageRepository.save(img);
        });

        AlbumImage image = albumImageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Imagem não encontrada"));

        if (!image.getAlbum().getId().equals(albumId)) {
            throw new RuntimeException("Imagem não pertence a este álbum");
        }

        image.setIsDefault(true);
        AlbumImage savedImage = albumImageRepository.save(image);

        String presignedUrl = minioStorageService.getPresignedUrl(savedImage.getFileName());
        savedImage.setFileUrl(presignedUrl);

        return savedImage;
    }
}
