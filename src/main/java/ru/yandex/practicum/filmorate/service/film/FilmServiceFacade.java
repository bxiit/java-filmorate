package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.util.enums.search.SearchBy;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FilmServiceFacade {
    private final FilmService filmService;
    private final EventService eventService;

    public FilmDto addFilm(FilmDto request) {
        return filmService.addFilm(request);
    }

    public FilmDto findFilmById(long filmId) {
        return filmService.findFilmById(filmId);
    }

    public List<FilmDto> findFilmsByDirector(Long directorId, String sort) {
        return filmService.findFilmsByDirector(directorId, sort);
    }

    public List<FilmDto> findAllFilms() {
        return filmService.findAllFilms();
    }

    public FilmDto updateFilm(FilmDto request) {
        return filmService.updateFilm(request);
    }

    public void deleteFilmById(long filmId) {
        filmService.deleteFilmById(filmId);
    }

    public List<FilmDto> findPopularFilmsByCount(int count) {
        return filmService.findPopularFilmsByCount(count);
    }

    public List<FilmDto> findPopularFilmsByGenreAndYear(int count, Long genreId, Integer year) {
        return filmService.findPopularFilmsByGenreAndYear(count, genreId, year);
    }

    public List<FilmDto> findFilmsByQuery(String search, SearchBy[] by) {
        return filmService.findFilmsByQuery(search, by);
    }

    public void likeFilm(long filmId, long userId) {
        filmService.likeFilm(filmId, userId);
        eventService.createEvent(userId, filmId, EventType.LIKE, Operation.ADD);
    }

    public void unlikeFilm(long filmId, long userId) {
        filmService.unlikeFilm(filmId, userId);
        eventService.createEvent(userId, filmId, EventType.LIKE, Operation.REMOVE);
    }

    public List<FilmDto> getFilmRecommendations(long userId) {
        return filmService.getFilmRecommendations(userId);
    }

    public List<FilmDto> commonFilmsWithFriend(long userId, long friendId) {
        return filmService.commonFilmsWithFriend(userId, friendId);
    }
}
