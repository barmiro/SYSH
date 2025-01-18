CREATE TABLE Streams (
    ts timestamp NOT NULL,
    ms_played integer NOT NULL,
    spotify_track_id varchar NOT NULL
);

CREATE TABLE Tracks (
    spotify_track_id varchar UNIQUE NOT NULL,
    stream_number integer NOT NULL DEFAULT 0,
    total_ms_played integer NOT NULL DEFAULT 0,
    first_played timestamp,
    stream_count integer
);

CREATE TABLE Albums (
);

CREATE TABLE Artists (
);