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
@Schema(description = "DTO resumido para álbum (usado em listagens)")
public class AlbumSummaryDTO {

    @Schema(description = "ID do álbum", example = "1")
    private Long id;

    @Schema(description = "Título do álbum", example = "Abbey Road")
    private String title;
}

