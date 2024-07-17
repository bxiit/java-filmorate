package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping()
    @ResponseStatus(CREATED)
    public FilmDto addFilm(@Valid @RequestBody FilmDto film) {
        return filmService.addFilm(film);
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public FilmDto getFilmById(@PathVariable("id") long id) {
        return filmService.findFilmById(id);
    }

    @GetMapping()
    @ResponseStatus(OK)
    public List<FilmDto> getFilms() {
        return filmService.findAllFilms();
    }

    @PutMapping()
    @ResponseStatus(OK)
    public FilmDto updateFilm(@Valid @RequestBody FilmDto request) {
        return filmService.updateFilm(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteFilmById(@PathVariable("id") long filmId) {
        filmService.deleteFilmById(filmId);
    }

    @GetMapping("/popular")
    @ResponseStatus(OK)
    public List<FilmDto> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10") int count,
            @RequestParam(value = "genreId", required = false) Long genreId,
            @RequestParam(value = "year", required = false) Integer year) {
        if (genreId == null && year == null) {
            return filmService.findPopularFilmsByCount(count);
        } else {
            return filmService.findPopularFilmsByGenreAndYear(count, genreId, year);
        }
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(OK)
    public void likeFilm(@PathVariable("userId") long userId, @PathVariable("id") long filmId) {
        filmService.likeFilm(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(OK)
    public void unlikeFilm(@PathVariable("userId") long userId, @PathVariable("id") long filmId) {
        filmService.unlikeFilm(filmId, userId);
    }
}
