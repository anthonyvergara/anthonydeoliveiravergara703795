package com.anthony.backend.domain.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artist {

    private Long id;
    private String name;

    @Builder.Default
    private List<Album> albums = new ArrayList<>();

    public void addAlbum(Album album) {
        boolean exists = albums.stream()
                .anyMatch(a -> a.getTitle().equalsIgnoreCase(album.getTitle()));

        if (exists) {
            throw new RuntimeException("Artista já tem um álbum com este título");
        }
        albums.add(album);
        album.setArtist(this);
    }

    public void removeAlbum(Album album) {
        albums.remove(album);
        album.setArtist(null);
    }
}
