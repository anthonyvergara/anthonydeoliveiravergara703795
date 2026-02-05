package com.anthony.backend.controller;

import com.anthony.backend.application.mapper.ArtistMapper;
import com.anthony.backend.application.service.ArtistService;
import com.anthony.backend.controller.dto.request.ArtistRequestDTO;
import com.anthony.backend.controller.dto.response.ArtistResponseDTO;
import com.anthony.backend.controller.dto.response.PageResponseDTO;
import com.anthony.backend.domain.exception.BaseExceptionController;
import com.anthony.backend.domain.model.Artist;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/artist")
@Tag(name = "Artistas", description = "API para gerenciamento de artistas")
public class ArtistController extends BaseExceptionController {

    private final ArtistService artistService;
    private final ArtistMapper artistMapper;

    public ArtistController(ArtistService artistService, ArtistMapper artistMapper) {
        this.artistService = artistService;
        this.artistMapper = artistMapper;
    }

    @PostMapping
    @Operation(summary = "Criar um novo artista", description = "Cria um novo artista no sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Artista criado com sucesso",
                    content = @Content(schema = @Schema(implementation = ArtistResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    public ResponseEntity<ArtistResponseDTO> create(@Valid @RequestBody ArtistRequestDTO request) {
        Artist artist = artistService.create(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(artistMapper.toResponseDTO(artist));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar artista por ID", description = "Retorna um artista específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artista encontrado",
                    content = @Content(schema = @Schema(implementation = ArtistResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado", content = @Content)
    })
    public ResponseEntity<ArtistResponseDTO> findById(
            @Parameter(description = "ID do artista") @PathVariable Long id) {
        Artist artist = artistService.findById(id);
        return ResponseEntity.ok(artistMapper.toResponseDTO(artist));
    }

    @GetMapping
    @Operation(summary = "Listar artistas com paginação e filtros",
            description = "Lista todos os artistas com suporte a paginação, ordenação e filtros por nome e título de álbum")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de artistas retornada com sucesso")
    })
    public ResponseEntity<PageResponseDTO<ArtistResponseDTO>> findAll(
            @Parameter(description = "Filtro por nome do artista") @RequestParam(required = false) String name,
            @Parameter(description = "Filtro por título do álbum") @RequestParam(required = false) String albumTitle,
            @Parameter(description = "Incluir álbuns na resposta") @RequestParam(required = false, defaultValue = "false") boolean albums,
            @Parameter(description = "Número da página (inicia em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação (ex: name, id)") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<Artist> artists = artistService.findAll(name, albumTitle, albums, pageable);
        Page<ArtistResponseDTO> responsePage = artists.map(artistMapper::toResponseDTO);
        return ResponseEntity.ok(PageResponseDTO.from(responsePage));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um artista", description = "Atualiza os dados de um artista existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Artista atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = ArtistResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado", content = @Content)
    })
    public ResponseEntity<ArtistResponseDTO> update(
            @Parameter(description = "ID do artista") @PathVariable Long id,
            @Valid @RequestBody ArtistRequestDTO request) {
        Artist artist = artistService.update(id, request.getName());
        return ResponseEntity.ok(artistMapper.toResponseDTO(artist));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um artista", description = "Remove um artista do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Artista deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Artista possui álbuns associados", content = @Content)
    })
    public ResponseEntity<Void> delete(@Parameter(description = "ID do artista") @PathVariable Long id) {
        artistService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
