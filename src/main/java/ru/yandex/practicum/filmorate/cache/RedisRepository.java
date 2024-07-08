package ru.yandex.practicum.filmorate.cache;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface RedisRepository {
    void add(Film film);

    Film find(Long id);

    List<Film> findPopularFilms();
}