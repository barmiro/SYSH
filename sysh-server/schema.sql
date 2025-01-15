CREATE TABLE Streams (
    ts timestamp NOT NULL,
    ms_played integer NOT NULL,
    master_metadata_track_name varchar,
    master_metadata_album_artist_name varchar,
    master_metadata_album_album_name varchar,
    spotify_track_uri varchar NOT NULL
    );