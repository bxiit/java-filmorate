INSERT INTO MPA (NAME)
VALUES ('G'),
       ('PG'),
       ('PG-13'),
       ('R'),
       ('NC-17');

INSERT INTO GENRE (NAME)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный'),
       ('Боевик');

INSERT INTO director (name)
VALUES ('Айсултан Сейтов'),
       ('Квентин Тарантино'),
       ('Вуди Аллен'),
       ('Джеймс Кэмерон'),
       ('Гай Ричи');

INSERT INTO EVENT_TYPES (EVENT_TYPE)
VALUES ('LIKE'),
       ('REVIEW'),
       ('FRIEND'),
       ('DISLIKE');

INSERT INTO OPERATIONS (OPERATION)
VALUES ('ADD'),
       ('UPDATE'),
       ('REMOVE');