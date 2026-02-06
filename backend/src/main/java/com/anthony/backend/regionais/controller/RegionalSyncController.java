package com.anthony.backend.regionais.controller;

import com.anthony.backend.regionais.service.RegionalSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
@Tag(name = "Sincronização", description = "Endpoints para sincronização de dados externos")
public class RegionalSyncController {

    private final RegionalSyncService regionalSyncService;

    @PostMapping("/regionais")
    @Operation(
        summary = "Sincronizar regionais",
        description = "Sincroniza dados de regionais da Polícia Civil com a tabela interna."
    )
    public ResponseEntity<Map<String, String>> sincronizarRegionais() {
        regionalSyncService.sincronizarRegionais();
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Sincronização de regionais executada com sucesso"
        ));
    }
}
