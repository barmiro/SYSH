--CREATE TYPE user_role AS ENUM ('ADMIN', 'USER');

CREATE TABLE Users (
    username VARCHAR(64) PRIMARY KEY,
    password VARCHAR NOT NULL,
    spotify_state VARCHAR,
    access_token VARCHAR,
    refresh_token VARCHAR,
    token_expires TIMESTAMP,
    role VARCHAR DEFAULT 'USER',
--    consider handling the default in java
    display_name VARCHAR DEFAULT 'unknown username',
    timezone VARCHAR
);

CREATE TABLE SongStreams (
    id SERIAL PRIMARY KEY,
    ts TIMESTAMPTZ NOT NULL,
    username VARCHAR REFERENCES Users(username),
    ms_played INTEGER NOT NULL,
    spotify_track_id VARCHAR NOT NULL,
    CONSTRAINT no_duplicates UNIQUE (ts, spotify_track_id, username)
);


CREATE TABLE Tracks (
    spotify_track_id VARCHAR PRIMARY KEY,
    name VARCHAR,
    duration_ms INTEGER,
    album_id VARCHAR,
    disc_number INTEGER,
    track_number INTEGER
);

CREATE INDEX duration_name ON Tracks (duration_ms, name);

CREATE TABLE Track_Duplicates (
    primary_id VARCHAR REFERENCES Tracks(spotify_track_id),
    secondary_id VARCHAR REFERENCES Tracks(spotify_track_id),
    PRIMARY KEY (primary_id, secondary_id)
);

CREATE TABLE Albums (
    id VARCHAR PRIMARY KEY,
    name VARCHAR,
    total_tracks INTEGER,
    release_date VARCHAR,
    image_url VARCHAR,
    thumbnail_url VARCHAR
);

CREATE TABLE Album_Tracklist (
    album_id VARCHAR REFERENCES Albums(id),
    spotify_track_id VARCHAR,
    disc_number INTEGER,
    track_number INTEGER,
    PRIMARY KEY (album_id, spotify_track_id)
);

CREATE INDEX tracklist_index ON Album_Tracklist (album_id);


CREATE TABLE Artists (
    id VARCHAR PRIMARY KEY,
    name VARCHAR,
    image_url VARCHAR,
    thumbnail_url VARCHAR
);


CREATE TABLE Albums_Tracks (
    album_id VARCHAR REFERENCES Albums(id),
    spotify_track_id VARCHAR REFERENCES Tracks(spotify_track_id),
    disc_number INTEGER,
    track_number INTEGER,
    PRIMARY KEY (album_id, spotify_track_id)
);

CREATE INDEX album_join ON Albums_Tracks (album_id);

CREATE TABLE Tracks_Artists (
    spotify_track_id VARCHAR REFERENCES Tracks(spotify_track_id),
    artist_id VARCHAR REFERENCES Artists(id),
    artist_order INTEGER,
    PRIMARY KEY (spotify_track_id, artist_id)
);

CREATE TABLE Artists_Albums (
    artist_id VARCHAR REFERENCES Artists(id),
    album_id VARCHAR REFERENCES Albums(id),
    artist_order INTEGER,
    PRIMARY KEY (artist_id, album_id)
);


CREATE TABLE Stats_Cache_Range (
    username VARCHAR,
    start_date TIMESTAMP NOT NULL, 
    end_date TIMESTAMP NOT NULL, 
    minutes_streamed INTEGER, 
    stream_count INTEGER, 
    track_count INTEGER, 
    album_count INTEGER, 
    artist_count INTEGER,
    CONSTRAINT no_duplicate_stats UNIQUE (username, start_date, end_date)
);

CREATE TABLE Stats_Cache_Full (
    username VARCHAR PRIMARY KEY REFERENCES Users(username) ON DELETE CASCADE,
    minutes_streamed INTEGER DEFAULT 0, 
    stream_count INTEGER DEFAULT 0, 
    track_count INTEGER DEFAULT 0, 
    album_count INTEGER DEFAULT 0, 
    artist_count INTEGER DEFAULT 0
);

CREATE FUNCTION create_stats_cache_full()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO Stats_Cache_Full (
    username
    )
    VALUES (NEW.username);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER user_insert_trigger
AFTER INSERT ON Users
FOR EACH ROW
EXECUTE FUNCTION create_stats_cache_full();

--INSERT INTO Stats_Cache_Full (
--    minutes_streamed,
--    stream_count,
--    track_count,
--    album_count,
--    artist_count)
--    VALUES (
--    0, 0, 0, 0, 0
--);

CREATE TABLE Top_Albums_Cache (
    username VARCHAR,
    id VARCHAR,
    name VARCHAR,
    thumbnail_url VARCHAR,
    primary_artist_name VARCHAR,
    stream_count INTEGER DEFAULT 0,
    total_ms_played INTEGER DEFAULT 0,
    PRIMARY KEY (username, id),
    FOREIGN KEY (username) REFERENCES Users(username) ON DELETE CASCADE
);
CREATE INDEX albums_by_time ON Top_Albums_Cache (username, total_ms_played DESC);
CREATE INDEX albums_by_count ON Top_Albums_Cache (username, stream_count DESC);

CREATE TABLE Top_Tracks_Cache (
    username VARCHAR,
    spotify_track_id VARCHAR,
    name VARCHAR,
    album_name VARCHAR,
    thumbnail_url VARCHAR,
    primary_artist_name VARCHAR,
    stream_count INTEGER DEFAULT 0,
    total_ms_played INTEGER DEFAULT 0,
    PRIMARY KEY (username, spotify_track_id),
    FOREIGN KEY (username) REFERENCES Users(username) ON DELETE CASCADE
);

CREATE INDEX tracks_by_time ON Top_Tracks_Cache (username, total_ms_played DESC);
CREATE INDEX tracks_by_count ON Top_Tracks_Cache (username, stream_count DESC);





