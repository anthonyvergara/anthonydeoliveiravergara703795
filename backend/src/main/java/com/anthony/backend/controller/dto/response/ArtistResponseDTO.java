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
@Schema(description = "DTO para resposta de artista")
public class ArtistResponseDTO {

    @Schema(description = "ID do artista", example = "1")
    private Long id;

    @Schema(description = "Nome do artista", example = "The Beatles")
    private String name;

    @Schema(description = "Lista de álbuns do artista")
    private List<AlbumSummaryDTO> albums;
}

