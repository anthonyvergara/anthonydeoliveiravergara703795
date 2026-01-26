package com.anthony.backend.controller.dto;

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

    @Schema(description = "Nome do arquivo", example = "cover.jpg")
    private String fileName;

    @Schema(description = "URL do arquivo", example = "https://example.com/images/cover.jpg")
    private String fileUrl;

    @Schema(description = "Indica se é a imagem padrão", example = "true")
    private Boolean isDefault;
}
