package com.anthony.backend.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para resposta de álbum")
public class AlbumResponseDTO {

    @Schema(description = "ID do álbum", example = "1")
    private Long id;

    @Schema(description = "Título do álbum", example = "Abbey Road")
    private String title;

    @Schema(description = "Nome do artista", example = "The Beatles")
    private String artistName;

    @Schema(description = "ID do artista", example = "1")
    private Long artistId;

    @Schema(description = "Lista de imagens do álbum")
    private List<AlbumImageDTO> images;
}

