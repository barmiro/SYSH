CREATE TABLE Streams (
    ts timestamp NOT NULL,
    ms_played integer NOT NULL,
    master_metadata_track_name varchar,
    master_metadata_album_artist_name varchar,
    master_metadata_album_album_name varchar,
    spotify_track_uri varchar NOT NULL
);

CREATE TABLE Tracks (
    spotify_track_uri varchar UNIQUE NOT NULL,
    master_metadata_track_name varchar,
    master_metadata_album_artist_name varchar,
    master_metadata_album_album_name varchar,
    stream_number integer NOT NULL DEFAULT 0,
    total_ms_played integer NOT NULL DEFAULT 0,
    first_played timestamp
);