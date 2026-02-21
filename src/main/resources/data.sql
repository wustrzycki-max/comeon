delete from loves;
delete from games;
delete from players;

INSERT INTO games (name) VALUES ('Chess');
INSERT INTO games (name) VALUES ('Checkers');
INSERT INTO games (name) VALUES ('Minecraft');
INSERT INTO games (name) VALUES ('Roblox');
INSERT INTO games (name) VALUES ('Avatar World');
INSERT INTO games (name) VALUES ('Toca Life World');

INSERT INTO players (name) VALUES ('Michelle');
INSERT INTO players (name) VALUES ('Agnes');
INSERT INTO players (name) VALUES ('Alina');

INSERT INTO loves (player_id, game_id) VALUES
((select id from players where name = 'Michelle'), (select id from games where name = 'Toca Life World'));
INSERT INTO loves (player_id, game_id) VALUES
((select id from players where name = 'Alina'), (select id from games where name = 'Avatar World'));
INSERT INTO loves (player_id, game_id) VALUES
((select id from players where name = 'Alina'), (select id from games where name = 'Toca Life World'));
INSERT INTO loves (player_id, game_id) VALUES
((select id from players where name = 'Alina'), (select id from games where name = 'Roblox'));
INSERT INTO loves (player_id, game_id) VALUES
((select id from players where name = 'Alina'), (select id from games where name = 'Chess'));
INSERT INTO loves (player_id, game_id) VALUES
((select id from players where name = 'Agnes'), (select id from games where name = 'Roblox'));
INSERT INTO loves (player_id, game_id) VALUES
((select id from players where name = 'Agnes'), (select id from games where name = 'Minecraft'));