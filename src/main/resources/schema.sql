CREATE TABLE IF NOT EXISTS rating_MPA (
    mpa_id SERIAL NOT NULL PRIMARY KEY,
    mpa_name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS films (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL,
    description VARCHAR,
    release_date TIMESTAMP,
    duration BIGINT,
    rating_id INT NOT NULL REFERENCES rating_MPA(mpa_id)
);

CREATE TABLE IF NOT EXISTS genres (
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genres (
    film_id BIGINT NOT NULL REFERENCES films(id),
    genre_id INT NOT NULL REFERENCES genres(id),
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    email VARCHAR NOT NULL,
    login VARCHAR NOT NULL,
    name VARCHAR NOT NULL,
    birthday TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_friends (
    user_id BIGINT NOT NULL REFERENCES users(id),
    friend_id BIGINT NOT NULL REFERENCES users(id),
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS film_likes (
    film_id BIGINT NOT NULL REFERENCES films(id),
    user_liked_id BIGINT NOT NULL REFERENCES users(id),
    PRIMARY KEY (film_id, user_liked_id)
);