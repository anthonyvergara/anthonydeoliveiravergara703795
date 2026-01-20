package com.anthony.backend.application.mapper;

import com.anthony.backend.domain.model.Album;
import com.anthony.backend.infrastructure.persistence.entity.AlbumEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AlbumImageMapper.class})
public interface AlbumMapper {

    AlbumEntity toEntity(Album album);

    Album toDomain(AlbumEntity entity);
}
