CREATE TABLE schema_version (
   schema_version INTEGER NOT NULL PRIMARY KEY,
   update_date DATE NOT NULL
);

CREATE TABLE role (
   id SERIAL PRIMARY KEY,
   name VARCHAR(256) NOT NULL,
   CONSTRAINT unique_role_name UNIQUE (name)
);

INSERT INTO role(name) VALUES ('admin');
INSERT INTO role(name) VALUES ('player');

CREATE TABLE user_ (
   id SERIAL PRIMARY KEY,
   username VARCHAR(256) NOT NULL,
   firstname VARCHAR(256) NOT NULL,
   lastname VARCHAR(256) NOT NULL,
   roleid INTEGER NOT NULL,
   CONSTRAINT unique_user_username UNIQUE (username),
   CONSTRAINT fk_role FOREIGN KEY (roleid) REFERENCES role(id)
);

CREATE TABLE tournament (
   id SERIAL PRIMARY KEY,
   name VARCHAR(256) NOT NULL,
   startdate DATE NOT NULL,
   enddate DATE NOT NULL,
   playoffteamsperconference INTEGER NOT NULL DEFAULT 0,
   active BOOLEAN,
   CONSTRAINT u_tournament_name UNIQUE (name)
);

CREATE TABLE conference (
   id SERIAL PRIMARY KEY,
   name VARCHAR(256) NOT NULL,
   tournamentid INTEGER NOT NULL,
   gamesperplayer INTEGER NOT NULL DEFAULT 1,
   CONSTRAINT u_conference_tournament_name UNIQUE (tournamentid, name),
   CONSTRAINT fk_tournament FOREIGN KEY (tournamentid) REFERENCES tournament(id)
);

CREATE TABLE team (
   id SERIAL PRIMARY KEY,
   name VARCHAR(256) NOT NULL,
   CONSTRAINT u_team_name UNIQUE (name)
);

CREATE TABLE player (
   id SERIAL PRIMARY KEY,
   userid INTEGER NOT NULL,
   conferenceid INTEGER NOT NULL,
   teamid INTEGER,
   CONSTRAINT u_player_user_conference UNIQUE (userid, conferenceid),
   CONSTRAINT fk_conference FOREIGN KEY (conferenceid) REFERENCES conference(id),
   CONSTRAINT fk_team FOREIGN KEY (teamid) REFERENCES team(id),
   CONSTRAINT fk_user FOREIGN KEY (userid) REFERENCES user_(id)
);

CREATE TABLE game (
   id SERIAL PRIMARY KEY,
   homeplayerid INTEGER NOT NULL,
   awayplayerid INTEGER NOT NULL,
   homegoals INTEGER NOT NULL DEFAULT 0,
   awaygoals INTEGER NOT NULL DEFAULT 0,
   overtime BOOLEAN NOT NULL DEFAULT false,
   shootout BOOLEAN NOT NULL DEFAULT false,
   modifieddate DATE,
   playdate DATE,
   CONSTRAINT fk_homeplayer FOREIGN KEY (homeplayerid) REFERENCES player(id) ON DELETE CASCADE,
   CONSTRAINT fk_awayplayer FOREIGN KEY (awayplayerid) REFERENCES player(id) ON DELETE CASCADE
);

INSERT INTO schema_version VALUES(1, NOW());

COMMIT;
