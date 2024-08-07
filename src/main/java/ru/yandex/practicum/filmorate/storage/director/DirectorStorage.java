package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    List<Director> findAllDirectors();

    Optional<Director> findDirectorById(Long directorId);

    List<Long> findFilmIdsByDirectorId(Long directorId);

    Director addDirector(Director director);

    void addDirectorsForFilm(Long filmId, List<Director> director);

    Director updateDirector(Director director);

    void deleteDirector(Long directorId);

    void deleteDirectorOfFilm(Long filmId);

    void load(List<Film> films);
}
