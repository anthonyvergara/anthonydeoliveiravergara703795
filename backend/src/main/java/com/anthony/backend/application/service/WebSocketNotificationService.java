package com.anthony.backend.application.service;

import com.anthony.backend.controller.dto.notification.AlbumNotificationDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifyAlbumCreated(Long albumId, String title, String artistName, Long artistId) {
        AlbumNotificationDTO notification = AlbumNotificationDTO.builder()
                .id(albumId)
                .title(title)
                .artistName(artistName)
                .artistId(artistId)
                .message("Novo álbum criado: " + title)
                .timestamp(LocalDateTime.now())
                .type("ALBUM_CREATED")
                .build();

        messagingTemplate.convertAndSend("/topic/albums", notification);
    }

    public void notifyAlbumUpdated(Long albumId, String title, String artistName, Long artistId) {
        AlbumNotificationDTO notification = AlbumNotificationDTO.builder()
                .id(albumId)
                .title(title)
                .artistName(artistName)
                .artistId(artistId)
                .message("Álbum atualizado: " + title)
                .timestamp(LocalDateTime.now())
                .type("ALBUM_UPDATED")
                .build();

        messagingTemplate.convertAndSend("/topic/albums", notification);
    }

    public void notifyAlbumDeleted(Long albumId, String title) {
        AlbumNotificationDTO notification = AlbumNotificationDTO.builder()
                .id(albumId)
                .title(title)
                .message("Álbum deletado: " + title)
                .timestamp(LocalDateTime.now())
                .type("ALBUM_DELETED")
                .build();

        messagingTemplate.convertAndSend("/topic/albums", notification);
    }
}

