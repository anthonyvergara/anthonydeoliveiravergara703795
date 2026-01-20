package com.anthony.backend.domain.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Album {

    private Long id;
    private String title;
    private Artist artist;

    @Builder.Default
    private List<AlbumImage> images = new ArrayList<>();

    public void addImage(AlbumImage image) {
        images.add(image);
        image.setAlbum(this);
    }

    public void removeImage(AlbumImage image) {
        images.remove(image);
        image.setAlbum(null);
    }
}
