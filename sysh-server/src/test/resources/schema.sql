CREATE TABLE Streams (
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

CREATE TABLE Stats_Cache (
    start_date TIMESTAMP NOT NULL, 
    end_date TIMESTAMP NOT NULL, 
    minutes_streamed INTEGER, 
    stream_count INTEGER, 
    track_count INTEGER, 
    album_count INTEGER, 
    artist_count INTEGER,
    CONSTRAINT no_duplicate_stats UNIQUE (start_date, end_date)
);

CREATE TABLE Refresh (token varchar);




