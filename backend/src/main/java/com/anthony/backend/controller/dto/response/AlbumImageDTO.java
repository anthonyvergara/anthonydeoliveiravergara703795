package com.anthony.backend.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para imagem do álbum")
public class AlbumImageDTO {

    @Schema(description = "ID da imagem", example = "1")
    private Long id;

    @Schema(description = "Chave do arquivo no storage", example = "albums/1/cover.jpg")
    private String fileKey;

    @Schema(description = "URL pré-assinada do arquivo", example = "https://minio.example.com/...")
    private String fileUrl;

    @Schema(description = "Indica se é a imagem padrão", example = "true")
    private Boolean isDefault;
}
