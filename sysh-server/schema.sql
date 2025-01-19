CREATE TABLE Albums (
    id varchar PRIMARY KEY
);

CREATE TABLE Artists (
    id varchar PRIMARY KEY
);

CREATE TABLE Tracks (
    spotify_track_id varchar PRIMARY KEY,
    name varchar,
    duration_ms integer,
    album_id varchar
);

CREATE TABLE Streams (
    id SERIAL PRIMARY KEY,
    ts timestamp NOT NULL,
    ms_played integer NOT NULL,
    spotify_track_id varchar NOT NULL
);

CREATE TABLE Tracks_Artists (
    spotify_track_id varchar REFERENCES Tracks(spotify_track_id),
    artist_id varchar REFERENCES Artists(id),
    artist_order integer,
    PRIMARY KEY (spotify_track_id, artist_id)
)
