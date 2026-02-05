package com.anthony.backend.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Resposta paginada genérica")
public class PageResponseDTO<T> {

    @Schema(description = "Lista de itens da página atual")
    private List<T> content;

    @Schema(description = "Número da página atual (inicia em 0)", example = "0")
    private int page;

    @Schema(description = "Quantidade de itens por página", example = "10")
    private int size;

    @Schema(description = "Total de elementos disponíveis", example = "120")
    private long totalElements;

    @Schema(description = "Total de páginas disponíveis", example = "12")
    private int totalPages;

    public static <T> PageResponseDTO<T> from(Page<T> page) {
        return PageResponseDTO.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}

