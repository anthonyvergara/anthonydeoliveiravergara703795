package com.anthony.backend.application.mapper;

import com.anthony.backend.domain.model.Album;
import com.anthony.backend.controller.dto.AlbumCreateUpdateResponseDTO;
import com.anthony.backend.controller.dto.AlbumResponseDTO;
import com.anthony.backend.infrastructure.persistence.entity.AlbumEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {AlbumImageMapper.class})
public interface AlbumMapper {

    @Mapping(target = "artist.albums", ignore = true)
    AlbumEntity toEntity(Album album);

    @Mapping(target = "artist.albums", ignore = true)
    Album toDomain(AlbumEntity entity);

    @Mapping(target = "artistName", source = "artist.name")
    @Mapping(target = "artistId", source = "artist.id")
    AlbumResponseDTO toResponseDTO(Album album);

    @Mapping(target = "artistName", source = "artist.name")
    @Mapping(target = "artistId", source = "artist.id")
    AlbumCreateUpdateResponseDTO toCreateUpdateResponseDTO(Album album);
}
