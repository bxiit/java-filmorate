package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.dto.film.FilmDto;

import java.util.List;

public interface FilmService {
    FilmDto addFilm(FilmDto request);

    FilmDto findFilmById(long filmId);

    List<FilmDto> findFilmsByDirector(Long directorId, String sort);

    List<FilmDto> findAllFilms();

    FilmDto updateFilm(FilmDto request);

    void deleteFilmById(long filmId);

    List<FilmDto> findPopularFilmsByCount(int count);

    List<FilmDto> findPopularFilmsByGenreAndYear(int count, Long genreId, Integer year);

    List<FilmDto> findFilmsByQuery(String search, String by);

    List<FilmDto> findFilmsByQueryOpt(String search, String by);

    void likeFilm(long filmId, long userId);

    void unlikeFilm(long filmId, long userId);

    List<FilmDto> getFilmRecommendations(long userId);

    List<FilmDto> commonFilmsWithFriend(long userId, long friendId);
}
