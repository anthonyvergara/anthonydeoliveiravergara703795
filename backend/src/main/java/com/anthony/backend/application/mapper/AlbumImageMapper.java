package com.anthony.backend.application.mapper;

import com.anthony.backend.controller.dto.AlbumImageDTO;
import com.anthony.backend.domain.model.AlbumImage;
import com.anthony.backend.infrastructure.persistence.entity.AlbumImageEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AlbumImageMapper {
    
    AlbumImageEntity toEntity(AlbumImage albumImage);
    
    AlbumImage toDomain(AlbumImageEntity entity);

    AlbumImageDTO toDTO(AlbumImage albumImage);
}
