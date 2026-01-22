package com.anthony.backend.controller;


import com.anthony.backend.application.mapper.AlbumMapper;
import com.anthony.backend.application.service.AlbumService;
import com.anthony.backend.controller.dto.AlbumRequestDTO;
import com.anthony.backend.controller.dto.AlbumResponseDTO;
import com.anthony.backend.domain.model.Album;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/album")
@Tag(name = "Álbuns", description = "API para gerenciamento de álbuns")
public class AlbumController {

    private final AlbumService albumService;
    private final AlbumMapper albumMapper;

    public AlbumController(AlbumService albumService, AlbumMapper albumMapper) {
        this.albumService = albumService;
        this.albumMapper = albumMapper;
    }

    @PostMapping
    @Operation(summary = "Criar um novo álbum", description = "Cria um novo álbum associado a um artista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Álbum criado com sucesso",
                    content = @Content(schema = @Schema(implementation = AlbumResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Artista não encontrado", content = @Content)
    })
    public ResponseEntity<AlbumResponseDTO> create(@Valid @RequestBody AlbumRequestDTO request) {
        Album album = albumService.create(request.getTitle(), request.getArtistId());
        return ResponseEntity.status(HttpStatus.CREATED).body(albumMapper.toResponseDTO(album));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar álbum por ID", description = "Retorna um álbum específico pelo seu ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Álbum encontrado",
                    content = @Content(schema = @Schema(implementation = AlbumResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Álbum não encontrado", content = @Content)
    })
    public ResponseEntity<AlbumResponseDTO> findById(
            @Parameter(description = "ID do álbum") @PathVariable Long id) {
        Album album = albumService.findById(id);
        return ResponseEntity.ok(albumMapper.toResponseDTO(album));
    }

    @GetMapping
    @Operation(summary = "Listar álbuns com paginação e filtros",
            description = "Lista todos os álbuns com suporte a paginação, ordenação e filtros por título e nome do artista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de álbuns retornada com sucesso")
    })
    public ResponseEntity<Page<AlbumResponseDTO>> findAll(
            @Parameter(description = "Filtro por título do álbum") @RequestParam(required = false) String title,
            @Parameter(description = "Filtro por nome do artista") @RequestParam(required = false) String artistName,
            @Parameter(description = "Número da página (inicia em 0)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Quantidade de itens por página") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Campo para ordenação (ex: title, id)") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Direção da ordenação (ASC ou DESC)") @RequestParam(defaultValue = "ASC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<Album> albums = albumService.findAll(title, artistName, pageable);
        return ResponseEntity.ok(albums.map(albumMapper::toResponseDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar um álbum", description = "Atualiza os dados de um álbum existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Álbum atualizado com sucesso",
                    content = @Content(schema = @Schema(implementation = AlbumResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Álbum ou artista não encontrado", content = @Content)
    })
    public ResponseEntity<AlbumResponseDTO> update(
            @Parameter(description = "ID do álbum") @PathVariable Long id,
            @Valid @RequestBody AlbumRequestDTO request) {
        Album album = albumService.update(id, request.getTitle(), request.getArtistId());
        return ResponseEntity.ok(albumMapper.toResponseDTO(album));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar um álbum", description = "Remove um álbum do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Álbum deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Álbum não encontrado", content = @Content)
    })
    public ResponseEntity<Void> delete(@Parameter(description = "ID do álbum") @PathVariable Long id) {
        albumService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
