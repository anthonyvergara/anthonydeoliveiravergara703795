package com.anthony.backend.controller.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumNotificationDTO {
    private Long id;
    private String title;
    private String artistName;
    private Long artistId;
    private String message;
    private LocalDateTime timestamp;
    private String type;
}

