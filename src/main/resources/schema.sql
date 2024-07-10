-- Truncate tables
-- TRUNCATE TABLE film_genre;
-- TRUNCATE TABLE film_user_likes;
-- TRUNCATE TABLE friends;
-- TRUNCATE TABLE film;
-- TRUNCATE TABLE genre;
-- TRUNCATE TABLE mpa;
-- TRUNCATE TABLE users;

-- Drop tables
DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS film_user_likes;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS film;
DROP TABLE IF EXISTS genre;
DROP TABLE IF EXISTS mpa;
DROP TABLE IF EXISTS users;
CREATE TABLE IF NOT EXISTS users (
                                     user_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     email TEXT UNIQUE NOT NULL,
                                     login TEXT UNIQUE NOT NULL,
                                     name TEXT,
                                     birthday DATE NOT NULL
);

CREATE TYPE IF NOT EXISTS friends_status AS ENUM ('REQ_USER1', 'REQ_USER2', 'FRIENDS');

CREATE TABLE IF NOT EXISTS friends (
                                       friends_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       user1_id BIGINT,
                                       user2_id BIGINT,
                                       status friends_status,
                                       CONSTRAINT user1_id_fk FOREIGN KEY (user1_id)
                                           REFERENCES users(user_id) ON DELETE CASCADE,
                                       CONSTRAINT user2_id_fk FOREIGN KEY (user2_id)
                                           REFERENCES users(user_id) ON DELETE CASCADE,
                                       CONSTRAINT check_friend CHECK (user1_id < user2_id),
                                       CONSTRAINT unique_friends UNIQUE (user1_id, user2_id, status)
);

CREATE TABLE IF NOT EXISTS mpa (
                                   mpa_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                   name VARCHAR(15) UNIQUE
);

CREATE TABLE IF NOT EXISTS film (
                                    film_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                    name TEXT,
                                    description VARCHAR(200),
                                    release_date DATE,
                                    duration BIGINT,
                                    mpa_id BIGINT REFERENCES mpa(mpa_id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS genre (
                                     genre_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                                     name TEXT UNIQUE
);

CREATE TABLE IF NOT EXISTS film_genre (
                                          film_genre_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                          film_id BIGINT REFERENCES film(film_id) ON DELETE CASCADE,
                                          genre_id BIGINT REFERENCES genre(genre_id)
);

CREATE TABLE IF NOT EXISTS film_user_likes (
                                               film_user_likes BIGINT AUTO_INCREMENT PRIMARY KEY,
                                               film_id BIGINT REFERENCES film(film_id) ON DELETE CASCADE,
                                               user_id BIGINT REFERENCES users(user_id) ON DELETE CASCADE,
                                               CONSTRAINT unique_likes UNIQUE (film_id, user_id)
);
