DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS users
(
    user_id  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    TEXT UNIQUE NOT NULL,
    login    TEXT UNIQUE NOT NULL,
    name     TEXT,
    birthday DATE        NOT NULL
);

DROP TYPE IF EXISTS friends_status;
CREATE TYPE friends_status AS ENUM('REQ_USER1', 'REQ_USER2', 'FRIENDS');

CREATE TABLE IF NOT EXISTS friends
(
    friends_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
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
    mpa_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name   VARCHAR(15) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS film
(
    film_id      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         TEXT NOT NULL ,
    description  VARCHAR(200) NOT NULL ,
    release_date DATE NOT NULL ,
    duration     BIGINT NOT NULL ,
    mpa_id       BIGINT REFERENCES mpa (mpa_id)
);

CREATE TABLE IF NOT EXISTS genre
(
    genre_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name     TEXT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS film_genre
(
    film_genre_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id       BIGINT REFERENCES film (film_id) ON DELETE CASCADE,
    genre_id      BIGINT REFERENCES genre (genre_id)
);

CREATE TABLE IF NOT EXISTS film_user_likes
(
    film_user_likes BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id         BIGINT REFERENCES film (film_id) ON DELETE CASCADE,
    user_id         BIGINT REFERENCES users (user_id),
    CONSTRAINT unique_likes UNIQUE (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS review
(
    reviewId  BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    userId    BIGINT REFERENCES users (user_id),
    filmId    BIGINT REFERENCES film (film_id),
    content    VARCHAR(200) NOT NULL,
    isPositive BOOLEAN NOT NULL,
    useful     BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS director
(
    director_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id     BIGINT REFERENCES film (film_id),
    name        VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS operation
(
    operation_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS event_type
(
    event_type_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name          VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS review_user_likes
(
    review_id INTEGER NOT NULL REFERENCES review(reviewId),
    like_user_id INTEGER NOT NULL REFERENCES users(user_id),
    PRIMARY KEY (like_user_id, review_id)
);

CREATE TABLE IF NOT EXISTS review_user_dislikes
(
    review_id INTEGER NOT NULL REFERENCES review(reviewId),
    dislike_user_id INTEGER NOT NULL REFERENCES users(user_id),
    PRIMARY KEY (dislike_user_id, review_id)
);