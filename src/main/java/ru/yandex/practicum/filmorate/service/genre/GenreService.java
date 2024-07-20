package ru.yandex.practicum.filmorate.service.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreService {
    Genre findGenreById(long genreId);

    List<Genre> findAllGenres();

    boolean isExist(long genreId);
}
