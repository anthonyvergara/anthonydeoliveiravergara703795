package com.anthony.backend.application.mapper;

import com.anthony.backend.controller.dto.AlbumImageDTO;
import com.anthony.backend.domain.model.AlbumImage;
import com.anthony.backend.infrastructure.persistence.entity.AlbumImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AlbumImageMapper {
    
    AlbumImageEntity toEntity(AlbumImage albumImage);
    
    @Mapping(target = "album", ignore = true)
    AlbumImage toDomain(AlbumImageEntity entity);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "fileKey", source = "fileKey")
    @Mapping(target = "fileUrl", source = "fileUrl")
    @Mapping(target = "isDefault", source = "isDefault")
    AlbumImageDTO toDTO(AlbumImage albumImage);
}
