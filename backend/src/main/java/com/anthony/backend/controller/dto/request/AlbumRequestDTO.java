package com.anthony.backend.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para requisição de criação/atualização de álbum")
public class AlbumRequestDTO {

    @NotBlank(message = "O título do álbum é obrigatório")
    @Schema(description = "Título do álbum", example = "Abbey Road")
    private String title;

    @NotNull(message = "O ID do artista é obrigatório")
    @Schema(description = "ID do artista", example = "1")
    private Long artistId;
}

