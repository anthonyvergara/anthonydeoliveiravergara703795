package com.anthony.backend.application.mapper;

import com.anthony.backend.controller.dto.response.AlbumSummaryDTO;
import com.anthony.backend.controller.dto.response.ArtistResponseDTO;
import com.anthony.backend.domain.model.Album;
import com.anthony.backend.domain.model.Artist;
import com.anthony.backend.infrastructure.persistence.entity.ArtistEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AlbumMapper.class})
public interface ArtistMapper {

    @Mapping(target = "albums", ignore = true)
    ArtistEntity toEntity(Artist artist);

    @Mapping(target = "albums", ignore = true)
    Artist toDomain(ArtistEntity entity);

    @Mapping(target = "albums")
    Artist toDomainWithAlbums(ArtistEntity entity);

    ArtistResponseDTO toResponseDTO(Artist artist);

    AlbumSummaryDTO toAlbumSummaryDTO(Album album);
}









