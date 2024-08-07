package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(Film film);

    Optional<Film> findFilmById(long filmId);

    List<Film> findAllFilms();

    Film updateFilm(Film film);

    boolean deleteFilmById(long filmId);

    List<Film> findPopularFilms(long count);

    List<Film> findPopularFilmsByGenreAndYear(int count, Long genreId, Integer year);

    List<Film> findFilmsByQueryFilmTitle(String search);

    List<Film> findFilmsByQueryDirectorName(String search);

    boolean likeFilm(long filmId, long userId);

    boolean unlikeFilm(long filmId, long userId);

    List<Film> getFilmRecommendations(long userId);

    List<Film> getCommonFilmsIdsWithAnotherUser(long userId, long friendId);
}
