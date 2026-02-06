package com.anthony.backend.regionais.client;

import com.anthony.backend.regionais.dto.RegionalExternoDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegionaisClient {

    private final RestTemplate restTemplate;
    private static final String REGIONAIS_URL = "https://integrador-argus-api.geia.vip/v1/regionais";

    public List<RegionalExternoDTO> buscarRegionais() {
        try {
            log.info("Buscando regionais da API externa: {}", REGIONAIS_URL);

            ResponseEntity<List<RegionalExternoDTO>> response = restTemplate.exchange(
                    REGIONAIS_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<RegionalExternoDTO>>() {}
            );

            List<RegionalExternoDTO> regionais = response.getBody();
            log.info("Regionais obtidas com sucesso. Total: {}", regionais != null ? regionais.size() : 0);

            return regionais;
        } catch (Exception e) {
            log.error("Erro ao buscar regionais da API externa", e);
            throw new RuntimeException("Erro ao buscar regionais da API externa: " + e.getMessage(), e);
        }
    }
}

