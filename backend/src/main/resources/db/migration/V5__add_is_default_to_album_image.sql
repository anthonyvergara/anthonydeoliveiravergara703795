ALTER TABLE album_image ADD COLUMN is_default BOOLEAN NOT NULL DEFAULT false;

CREATE INDEX idx_album_image_is_default ON album_image(album_id, is_default);

