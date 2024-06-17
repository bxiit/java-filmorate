package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private static final HttpHeaders HTTP_HEADERS = new HttpHeaders();
    private final FilmService filmService;

    static {
        HTTP_HEADERS.setContentType(APPLICATION_JSON);
    }

    @PostMapping()
    public ResponseEntity<Film> addFilm(@RequestBody Film film) {
        Film createdFilm = filmService.addFilm(film);
        return new ResponseEntity<>(createdFilm, HTTP_HEADERS, CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable("id") long id) {
        Film film = filmService.findFilmById(id);
        return new ResponseEntity<>(film, HTTP_HEADERS, OK);
    }

    @GetMapping()
    public ResponseEntity<List<Film>> getFilms() {
        List<Film> allFilms = filmService.findAllFilms();
        return new ResponseEntity<>(allFilms, HTTP_HEADERS, OK);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        Film updatedFilm = filmService.updateFilm(film);
        return new ResponseEntity<>(updatedFilm, HTTP_HEADERS, OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFilmById(@PathVariable("id") long filmId) {
        filmService.deleteFilmById(filmId);
        return new ResponseEntity<>(HTTP_HEADERS, NO_CONTENT);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10") int count
    ) {
        List<Film> popularFilmsByCount = filmService.findPopularFilmsByCount(count);
        return new ResponseEntity<>(popularFilmsByCount, HTTP_HEADERS, OK);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> likeFilm(@PathVariable("userId") long userId, @PathVariable("id") long filmId) {
        filmService.likeFilm(filmId, userId);
        return new ResponseEntity<>(HTTP_HEADERS, OK);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> unlikeFilm(@PathVariable("userId") long userId, @PathVariable("id") long filmId) {
        filmService.unlikeFilm(filmId, userId);
        return new ResponseEntity<>(HTTP_HEADERS, OK);
    }
}
