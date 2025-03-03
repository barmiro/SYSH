CREATE TYPE user_role AS ENUM ('ADMIN', 'USER');

CREATE TABLE Users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR NOT NULL,
    role user_role DEFAULT 'USER',
    display_name VARCHAR
);

CREATE TABLE User_Data (
    id SERIAL PRIMARY KEY,
    display_name VARCHAR,
    CONSTRAINT only_one_user_data CHECK (id = 1)
);

INSERT INTO User_Data(display_name) VALUES ('username unknown');

CREATE TABLE SongStreams (
    id SERIAL PRIMARY KEY,
    ts timestamp NOT NULL,
    ms_played integer NOT NULL,
    spotify_track_id varchar NOT NULL,
    CONSTRAINT no_duplicates UNIQUE (ts, spotify_track_id)
);


CREATE TABLE Tracks (
    spotify_track_id varchar PRIMARY KEY,
    name varchar,
    duration_ms integer,
    album_id varchar,
    disc_number integer,
    track_number integer
);

CREATE INDEX duration_name ON Tracks (duration_ms, name);

CREATE TABLE Track_Duplicates (
    primary_id varchar REFERENCES Tracks(spotify_track_id),
    secondary_id varchar REFERENCES Tracks(spotify_track_id),
    PRIMARY KEY (primary_id, secondary_id)
);

CREATE TABLE Albums (
    id varchar PRIMARY KEY,
    name varchar,
    total_tracks integer,
    release_date varchar,
    image_url varchar,
    thumbnail_url varchar
);

CREATE TABLE Album_Tracklist (
    album_id varchar REFERENCES Albums(id),
    spotify_track_id varchar,
    disc_number integer,
    track_number integer,
    PRIMARY KEY (album_id, spotify_track_id)
);

CREATE INDEX tracklist_index ON Album_Tracklist (album_id);


CREATE TABLE Artists (
    id varchar PRIMARY KEY,
    name varchar,
    image_url varchar,
    thumbnail_url varchar
);


CREATE TABLE Albums_Tracks (
    album_id varchar REFERENCES Albums(id),
    spotify_track_id varchar REFERENCES Tracks(spotify_track_id),
    disc_number integer,
    track_number integer,
    PRIMARY KEY (album_id, spotify_track_id)
);

CREATE INDEX album_join ON Albums_Tracks (album_id);

CREATE TABLE Tracks_Artists (
    spotify_track_id varchar REFERENCES Tracks(spotify_track_id),
    artist_id varchar REFERENCES Artists(id),
    artist_order integer,
    PRIMARY KEY (spotify_track_id, artist_id)
);

CREATE TABLE Artists_Albums (
    artist_id varchar REFERENCES Artists(id),
    album_id varchar REFERENCES Albums(id),
    artist_order integer,
    PRIMARY KEY (artist_id, album_id)
);

CREATE TABLE Refresh (token varchar);


CREATE TABLE Stats_Cache_Range (
    start_date TIMESTAMP NOT NULL, 
    end_date TIMESTAMP NOT NULL, 
    minutes_streamed INTEGER, 
    stream_count INTEGER, 
    track_count INTEGER, 
    album_count INTEGER, 
    artist_count INTEGER,
    CONSTRAINT no_duplicate_stats UNIQUE (start_date, end_date)
);

CREATE TABLE Stats_Cache_Full (
    id SERIAL PRIMARY KEY,
    minutes_streamed INTEGER, 
    stream_count INTEGER, 
    track_count INTEGER, 
    album_count INTEGER, 
    artist_count INTEGER,
    CONSTRAINT only_one_full_cache CHECK (id = 1)
);
INSERT INTO Stats_Cache_Full (
    minutes_streamed,
    stream_count,
    track_count,
    album_count,
    artist_count)
    VALUES (
    0, 0, 0, 0, 0
);

CREATE TABLE Top_Albums_Cache (
    id VARCHAR,
    name VARCHAR,
    thumbnail_url VARCHAR,
    primary_artist_name VARCHAR,
    stream_count INTEGER DEFAULT 0,
    total_ms_played INTEGER DEFAULT 0
);
CREATE INDEX albums_by_time ON Top_Albums_Cache (total_ms_played DESC);
CREATE INDEX albums_by_count ON Top_Albums_Cache (stream_count DESC);

CREATE TABLE Top_Tracks_Cache (
    spotify_track_id VARCHAR,
    name VARCHAR,
    album_name VARCHAR,
    thumbnail_url VARCHAR,
    primary_artist_name VARCHAR,
    stream_count INTEGER DEFAULT 0,
    total_ms_played INTEGER DEFAULT 0
);

CREATE INDEX tracks_by_time ON Top_Tracks_Cache (total_ms_played DESC);
CREATE INDEX tracks_by_count ON Top_Tracks_Cache (stream_count DESC);



