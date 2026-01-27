package com.anthony.backend.controller.dto;

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
@Schema(description = "DTO de resposta para upload de imagens")
public class AlbumImageUploadResponseDTO {

    @Schema(description = "Lista de imagens carregadas")
    private List<AlbumImageDTO> images;

    @Schema(description = "Mensagem de sucesso")
    private String message;
}

