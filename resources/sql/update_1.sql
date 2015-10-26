-- Moving games per player from tournament to conference
ALTER TABLE conference ADD gamesperplayer INTEGER NOT NULL DEFAULT 1;
UPDATE conference c SET gamesperplayer = (SELECT gamesperplayer FROM tournament t where c.tournamentid = t.id);
ALTER TABLE tournament DROP COLUMN gamesperplayer;

-- Hipchat channel key for notifications to conference
ALTER TABLE conference ADD hipchatchannelid VARCHAR(256);


INSERT INTO schema_version VALUES(2, NOW());
