package com.anthony.backend.application.mapper;

import com.anthony.backend.domain.model.Artist;
import com.anthony.backend.infrastructure.persistence.entity.ArtistEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AlbumMapper.class})
public interface ArtistMapper {

    ArtistEntity toEntity(Artist artist);

    Artist toDomain(ArtistEntity entity);
}
