package com.anthony.backend.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para resposta de criação/atualização de álbum")
public class AlbumCreateUpdateResponseDTO {

    @Schema(description = "ID do álbum", example = "1")
    private Long id;

    @Schema(description = "Título do álbum", example = "Abbey Road")
    private String title;

    @Schema(description = "Nome do artista", example = "The Beatles")
    private String artistName;

    @Schema(description = "ID do artista", example = "1")
    private Long artistId;
}

