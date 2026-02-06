package com.anthony.backend.regionais.service;

import com.anthony.backend.regionais.client.RegionaisClient;
import com.anthony.backend.regionais.domain.Regional;
import com.anthony.backend.regionais.dto.RegionalExternoDTO;
import com.anthony.backend.regionais.repository.RegionalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegionalSyncService {

    private final RegionaisClient regionaisClient;
    private final RegionalRepository regionalRepository;

    @Transactional
    public void sincronizarRegionais() {
        log.info("Iniciando sincronização de regionais");

        // 1. Buscar regionais externas
        List<RegionalExternoDTO> regionaisExternas = regionaisClient.buscarRegionais();
        Map<Long, RegionalExternoDTO> regionaisExternasMap = regionaisExternas.stream()
                .collect(Collectors.toMap(RegionalExternoDTO::getId, r -> r));

        // 2. Buscar regionais ativas do banco
        List<Regional> regionaisAtivas = regionalRepository.findByAtivoTrue();
        Map<Long, Regional> regionaisAtivasMap = regionaisAtivas.stream()
                .collect(Collectors.toMap(Regional::getExternalId, r -> r));

        // Estatísticas
        int inseridos = 0;
        int atualizados = 0;
        int inativados = 0;

        // 3. Processar regionais externas
        for (RegionalExternoDTO regionalExterno : regionaisExternas) {
            Regional regionalExistente = regionaisAtivasMap.get(regionalExterno.getId());

            if (regionalExistente == null) {
                // Não existe no banco → inserir ativo
                Regional novaRegional = Regional.builder()
                        .externalId(regionalExterno.getId())
                        .nome(regionalExterno.getNome())
                        .ativo(true)
                        .build();
                regionalRepository.save(novaRegional);
                inseridos++;
                log.debug("Regional inserida: ID={}, Nome={}", regionalExterno.getId(), regionalExterno.getNome());
            } else if (!regionalExistente.getNome().equals(regionalExterno.getNome())) {
                // Existe e o nome mudou → inativar antigo + inserir novo
                regionalExistente.setAtivo(false);
                regionalRepository.save(regionalExistente);

                Regional novaRegional = Regional.builder()
                        .externalId(regionalExterno.getId())
                        .nome(regionalExterno.getNome())
                        .ativo(true)
                        .build();
                regionalRepository.save(novaRegional);
                atualizados++;
                log.debug("Regional atualizada: ID={}, Nome antigo={}, Nome novo={}",
                        regionalExterno.getId(), regionalExistente.getNome(), regionalExterno.getNome());
            }
            // Se existe e o nome não mudou → não faz nada
        }

        // 4. Inativar regionais que não vieram da API
        for (Regional regionalAtiva : regionaisAtivas) {
            if (!regionaisExternasMap.containsKey(regionalAtiva.getExternalId())) {
                regionalAtiva.setAtivo(false);
                regionalRepository.save(regionalAtiva);
                inativados++;
                log.debug("Regional inativada: ID={}, Nome={}",
                        regionalAtiva.getExternalId(), regionalAtiva.getNome());
            }
        }

        log.info("Sincronização concluída. Inseridos: {}, Atualizados: {}, Inativados: {}",
                inseridos, atualizados, inativados);
    }
}

