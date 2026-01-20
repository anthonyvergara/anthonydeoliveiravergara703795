CREATE TABLE album (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    artist_id BIGINT NOT NULL,
    CONSTRAINT fk_album_artist FOREIGN KEY (artist_id) REFERENCES artist(id) ON DELETE CASCADE
);

CREATE INDEX idx_album_artist_id ON album(artist_id);

