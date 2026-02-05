package com.anthony.backend.controller.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para requisição de criação/atualização de artista")
public class ArtistRequestDTO {

    @NotBlank(message = "O nome do artista é obrigatório")
    @Schema(description = "Nome do artista", example = "The Beatles")
    private String name;
}

