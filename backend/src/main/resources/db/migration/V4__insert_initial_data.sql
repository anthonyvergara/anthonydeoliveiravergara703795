-- Insert Artists
INSERT INTO artist (name) VALUES ('The Beatles');
INSERT INTO artist (name) VALUES ('Pink Floyd');
INSERT INTO artist (name) VALUES ('Led Zeppelin');
INSERT INTO artist (name) VALUES ('Queen');
INSERT INTO artist (name) VALUES ('The Rolling Stones');
INSERT INTO artist (name) VALUES ('Serj Tankian');
INSERT INTO artist (name) VALUES ('Mike Shinoda');
INSERT INTO artist (name) VALUES ('Michel Teló');
INSERT INTO artist (name) VALUES ('Guns N'' Roses');

-- Insert Albums for The Beatles
INSERT INTO album (title, artist_id) VALUES ('Abbey Road', (SELECT id FROM artist WHERE name = 'The Beatles'));
INSERT INTO album (title, artist_id) VALUES ('Sgt. Pepper''s Lonely Hearts Club Band', (SELECT id FROM artist WHERE name = 'The Beatles'));
INSERT INTO album (title, artist_id) VALUES ('The White Album', (SELECT id FROM artist WHERE name = 'The Beatles'));
INSERT INTO album (title, artist_id) VALUES ('Let It Be', (SELECT id FROM artist WHERE name = 'The Beatles'));
INSERT INTO album (title, artist_id) VALUES ('Revolver', (SELECT id FROM artist WHERE name = 'The Beatles'));

-- Insert Albums for Pink Floyd
INSERT INTO album (title, artist_id) VALUES ('The Dark Side of the Moon', (SELECT id FROM artist WHERE name = 'Pink Floyd'));
INSERT INTO album (title, artist_id) VALUES ('The Wall', (SELECT id FROM artist WHERE name = 'Pink Floyd'));
INSERT INTO album (title, artist_id) VALUES ('Wish You Were Here', (SELECT id FROM artist WHERE name = 'Pink Floyd'));
INSERT INTO album (title, artist_id) VALUES ('Animals', (SELECT id FROM artist WHERE name = 'Pink Floyd'));

-- Insert Albums for Led Zeppelin
INSERT INTO album (title, artist_id) VALUES ('Led Zeppelin IV', (SELECT id FROM artist WHERE name = 'Led Zeppelin'));
INSERT INTO album (title, artist_id) VALUES ('Physical Graffiti', (SELECT id FROM artist WHERE name = 'Led Zeppelin'));
INSERT INTO album (title, artist_id) VALUES ('Houses of the Holy', (SELECT id FROM artist WHERE name = 'Led Zeppelin'));

-- Insert Albums for Queen
INSERT INTO album (title, artist_id) VALUES ('A Night at the Opera', (SELECT id FROM artist WHERE name = 'Queen'));
INSERT INTO album (title, artist_id) VALUES ('Bohemian Rhapsody', (SELECT id FROM artist WHERE name = 'Queen'));
INSERT INTO album (title, artist_id) VALUES ('News of the World', (SELECT id FROM artist WHERE name = 'Queen'));

-- Insert Albums for The Rolling Stones
INSERT INTO album (title, artist_id) VALUES ('Exile on Main St.', (SELECT id FROM artist WHERE name = 'The Rolling Stones'));
INSERT INTO album (title, artist_id) VALUES ('Let It Bleed', (SELECT id FROM artist WHERE name = 'The Rolling Stones'));
INSERT INTO album (title, artist_id) VALUES ('Sticky Fingers', (SELECT id FROM artist WHERE name = 'The Rolling Stones'));

-- Insert Albums for Serj Tankian
INSERT INTO album (title, artist_id) VALUES ('Harakiri', (SELECT id FROM artist WHERE name = 'Serj Tankian'));
INSERT INTO album (title, artist_id) VALUES ('Black Blooms', (SELECT id FROM artist WHERE name = 'Serj Tankian'));
INSERT INTO album (title, artist_id) VALUES ('The Rough Dog', (SELECT id FROM artist WHERE name = 'Serj Tankian'));

-- Insert Albums for Mike Shinoda
INSERT INTO album (title, artist_id) VALUES ('The Rising Tied', (SELECT id FROM artist WHERE name = 'Mike Shinoda'));
INSERT INTO album (title, artist_id) VALUES ('Post Traumatic', (SELECT id FROM artist WHERE name = 'Mike Shinoda'));
INSERT INTO album (title, artist_id) VALUES ('Post Traumatic EP', (SELECT id FROM artist WHERE name = 'Mike Shinoda'));
INSERT INTO album (title, artist_id) VALUES ('Where''d You Go', (SELECT id FROM artist WHERE name = 'Mike Shinoda'));

-- Insert Albums for Michel Teló
INSERT INTO album (title, artist_id) VALUES ('Bem Sertanejo', (SELECT id FROM artist WHERE name = 'Michel Teló'));
INSERT INTO album (title, artist_id) VALUES ('Bem Sertanejo - O Show (Ao Vivo)', (SELECT id FROM artist WHERE name = 'Michel Teló'));
INSERT INTO album (title, artist_id) VALUES ('Bem Sertanejo - (1ª Temporada) - EP', (SELECT id FROM artist WHERE name = 'Michel Teló'));

-- Insert Albums for Guns N' Roses
INSERT INTO album (title, artist_id) VALUES ('Use Your Illusion I', (SELECT id FROM artist WHERE name = 'Guns N'' Roses'));
INSERT INTO album (title, artist_id) VALUES ('Use Your Illusion II', (SELECT id FROM artist WHERE name = 'Guns N'' Roses'));
INSERT INTO album (title, artist_id) VALUES ('Greatest Hits', (SELECT id FROM artist WHERE name = 'Guns N'' Roses'));
