package com.anthony.backend.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumImage {

    private Long id;
    private String fileKey;
    private String fileUrl;
    private Boolean isDefault;
    private Album album;
}
