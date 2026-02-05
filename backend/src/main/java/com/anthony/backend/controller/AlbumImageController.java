package com.anthony.backend.controller;

import com.anthony.backend.application.mapper.AlbumImageMapper;
import com.anthony.backend.application.service.AlbumImageService;
import com.anthony.backend.controller.dto.AlbumImageDTO;
import com.anthony.backend.controller.dto.AlbumImageUploadResponseDTO;
import com.anthony.backend.domain.exception.BaseExceptionController;
import com.anthony.backend.domain.model.AlbumImage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/album/{albumId}/images")
@Tag(name = "Imagens de Álbuns", description = "API para gerenciamento de imagens de álbuns")
@RequiredArgsConstructor
public class AlbumImageController extends BaseExceptionController {

    private final AlbumImageService albumImageService;
    private final AlbumImageMapper albumImageMapper;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de imagens do álbum",
               description = "Faz upload de uma ou mais imagens para o álbum. As imagens são armazenadas no MinIO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Imagens carregadas com sucesso",
                    content = @Content(schema = @Schema(implementation = AlbumImageUploadResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Álbum não encontrado", content = @Content)
    })
    public ResponseEntity<AlbumImageUploadResponseDTO> uploadImages(
            @Parameter(description = "ID do álbum") @RequestParam Long albumId,
            @RequestPart("files") MultipartFile[] files,
            @Parameter(description = "Definir primeira imagem como padrão") @RequestParam(required = false, defaultValue = "false") Boolean setAsDefault) {

        List<AlbumImage> uploadedImages = albumImageService.uploadImages(albumId, files, setAsDefault);

        List<AlbumImageDTO> imageDTOs = uploadedImages.stream()
                .map(albumImageMapper::toDTO)
                .collect(Collectors.toList());

        AlbumImageUploadResponseDTO response = AlbumImageUploadResponseDTO.builder()
                .images(imageDTOs)
                .message(String.format("%d imagem(ns) carregada(s) com sucesso", imageDTOs.size()))
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar imagens do álbum",
               description = "Retorna todas as imagens de um álbum com URLs pré-assinadas válidas por 30 minutos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de imagens retornada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Álbum não encontrado", content = @Content)
    })
    public ResponseEntity<List<AlbumImageDTO>> getAlbumImages(
            @Parameter(description = "ID do álbum") @RequestParam Long albumId) {

        List<AlbumImage> images = albumImageService.getAlbumImages(albumId);

        List<AlbumImageDTO> imageDTOs = images.stream()
                .map(albumImageMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(imageDTOs);
    }

    @GetMapping("/{imageId}")
    @Operation(summary = "Buscar imagem por ID",
               description = "Retorna uma imagem específica com URL pré-assinada válida por 30 minutos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagem encontrada",
                    content = @Content(schema = @Schema(implementation = AlbumImageDTO.class))),
            @ApiResponse(responseCode = "404", description = "Imagem não encontrada", content = @Content)
    })
    public ResponseEntity<AlbumImageDTO> getImageById(
            @Parameter(description = "ID da imagem") @PathVariable Long imageId) {

        AlbumImage image = albumImageService.getImageById(imageId);
        return ResponseEntity.ok(albumImageMapper.toDTO(image));
    }

    @PutMapping("/{imageId}/set-default")
    @Operation(summary = "Definir imagem como padrão",
               description = "Define uma imagem específica como a imagem padrão do álbum")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Imagem definida como padrão com sucesso",
                    content = @Content(schema = @Schema(implementation = AlbumImageDTO.class))),
            @ApiResponse(responseCode = "404", description = "Imagem ou álbum não encontrado", content = @Content)
    })
    public ResponseEntity<AlbumImageDTO> setAsDefault(
            @Parameter(description = "ID do álbum") @RequestParam Long albumId,
            @Parameter(description = "ID da imagem") @PathVariable Long imageId) {

        AlbumImage image = albumImageService.setAsDefault(albumId, imageId);
        return ResponseEntity.ok(albumImageMapper.toDTO(image));
    }

    @DeleteMapping("/{imageId}")
    @Operation(summary = "Deletar imagem",
               description = "Remove uma imagem do álbum e do storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Imagem deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Imagem não encontrada", content = @Content)
    })
    public ResponseEntity<Void> deleteImage(
            @Parameter(description = "ID da imagem") @PathVariable Long imageId) {

        albumImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}

