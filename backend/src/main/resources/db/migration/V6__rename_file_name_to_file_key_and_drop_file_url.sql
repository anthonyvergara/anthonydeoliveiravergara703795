ALTER TABLE album_image DROP COLUMN file_url;
ALTER TABLE album_image RENAME COLUMN file_name TO file_key;

