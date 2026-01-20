CREATE TABLE album_image (
    id BIGSERIAL PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL,
    file_url TEXT NOT NULL,
    album_id BIGINT NOT NULL,
    CONSTRAINT fk_album_image_album FOREIGN KEY (album_id) REFERENCES album(id) ON DELETE CASCADE
);

CREATE INDEX idx_album_image_album_id ON album_image(album_id);

