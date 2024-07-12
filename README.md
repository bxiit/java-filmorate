# java-filmorate
Template repository for Filmorate project.
Ссылка на ER-диаграмму схемы https://app.quickdatabasediagrams.com/#/d/pRqAbJ
![Схема базы данных модели фильма и пользователя](database-schema.png)

Для получения имени топ-10 фильмов по количеству лайков:
```postgresql
SELECT f.name,
       COUNT(ful.user_id)
FROM film f
LEFT JOIN film_user_likes ful ON f.film_id = ful.film_id
GROUP BY f.name;
```

Имена друзей пользователя с ID = 17.
```postgresql
WITH uid AS (
    SELECT 17 AS id
)

SELECT CASE
           WHEN user1_id = (SELECT * FROM uid) THEN user2_id
           ELSE user1_id
           END AS friend_id
FROM friends
WHERE (user1_id = (SELECT * FROM uid) OR user2_id = (SELECT * FROM uid)) AND status = 'FRIENDS';
```

Общие друзья пользователей с ID = 44 и ID = 17.
```postgresql
WITH u1id AS (
    SELECT 17 AS u1
),
     u2id AS (
         SELECT 44 AS u2
     )
SELECT CASE
           WHEN user1_id = (SELECT * FROM u1id) THEN user2_id
           ELSE user1_id
           END AS friend_id
FROM friends
WHERE (user1_id = (SELECT * FROM u1id) OR user2_id = (SELECT * FROM u1id)) AND status = 'FRIENDS'
INTERSECT
SELECT CASE
           WHEN user1_id = (SELECT * FROM u2id) THEN user2_id
           ELSE user1_id
           END AS friend_id
FROM friends
WHERE (user1_id = (SELECT * FROM u2id) OR user2_id = (SELECT * FROM u2id)) AND status = 'FRIENDS';
```
> [!IMPORTANT]
> <br> 
> ID пользователя отправивший запрос на дружбу другому пользователю будет записано с поле user1_id в таблице friends
> <br>
> А ID пользователя кому был отправлен запрос в колонке user2_id
> <br>

> [!WARNING]
> <br> 
> Таблица friends содержит поля user1_id и user2_id и поиск проводиться но обеим полям. Ведь сразу не понятно где может оказаться ID пользователя
> <br>