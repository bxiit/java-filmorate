DROP
ALL OBJECTS;
drop table if exists FILM_GENRE;
drop table if exists FILM_USER_LIKES;
drop table if exists film_director;
drop table if exists director;
drop table if exists GENRE;
drop table if exists review;
drop table if exists FILM;
drop table if exists MPA;
drop table if exists FRIENDS;
drop table if exists USERS;

CREATE TABLE IF NOT EXISTS users
(
    user_id  BIGINT PRIMARY KEY AUTO_INCREMENT,
    email    TEXT UNIQUE NOT NULL,
    login    TEXT UNIQUE NOT NULL,
    name     TEXT,
    birthday DATE        NOT NULL
);

CREATE TYPE friends_status AS ENUM ('REQ_USER1', 'REQ_USER2', 'FRIENDS');

CREATE TABLE IF NOT EXISTS friends
(
    friends_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user1_id   BIGINT,
    user2_id   BIGINT,
    status     friends_status,
    CONSTRAINT user1_id_fk FOREIGN KEY (user1_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT user2_id_fk FOREIGN KEY (user2_id) REFERENCES users (user_id) ON DELETE CASCADE,
    CONSTRAINT check_friend CHECK (user1_id < user2_id),
    CONSTRAINT unique_friends UNIQUE (user1_id, user2_id, status)
);

CREATE TABLE IF NOT EXISTS mpa
(
    mpa_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name   VARCHAR(15) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS film
(
    film_id      BIGINT PRIMARY KEY AUTO_INCREMENT,
    name         TEXT         NOT NULL,
    description  VARCHAR(200) NOT NULL,
    release_date DATE         NOT NULL,
    duration     BIGINT       NOT NULL,
    mpa_id       BIGINT REFERENCES mpa (mpa_id)
);

CREATE TABLE IF NOT EXISTS genre
(
    genre_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name     TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_genre_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    film_id       BIGINT REFERENCES film (film_id) ON DELETE CASCADE,
    genre_id      BIGINT REFERENCES genre (genre_id)
);

CREATE TABLE IF NOT EXISTS film_user_likes
(
    film_user_likes BIGINT AUTO_INCREMENT PRIMARY KEY,
    film_id         BIGINT REFERENCES film (film_id) ON DELETE CASCADE,
    user_id         BIGINT REFERENCES users (user_id),
    CONSTRAINT unique_likes UNIQUE (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS review
(
    review_id  BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT REFERENCES users (user_id),
    film_id    BIGINT REFERENCES film (film_id),
    content    VARCHAR(200) NOT NULL,
    isPositive BOOLEAN DEFAULT FALSE,
    useful     BIGINT       NOT NULL
);

CREATE TABLE IF NOT EXISTS director
(
    director_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS film_director
(
    film_director_id BIGINT AUTO_INCREMENT,
    film_id          BIGINT REFERENCES film (film_id),
    director_id      BIGINT REFERENCES director (director_id)
);

CREATE TABLE IF NOT EXISTS operation
(
    operation_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name         VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS event_type
(
    event_type_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name          VARCHAR(50) NOT NULL
);