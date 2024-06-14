package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
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
    public ResponseEntity<Film> addFilm(@RequestBody Film film) {
        Film createdFilm = filmService.addFilm(film);
        return new ResponseEntity<>(createdFilm, CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable("id") long id) {
        Film film = filmService.findFilmById(id)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        return new ResponseEntity<>(film, OK);
    }

    @GetMapping()
    public ResponseEntity<List<Film>> getFilms() {
        List<Film> allFilms = filmService.findAllFilms();
        return new ResponseEntity<>(allFilms, OK);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        Film updatedFilm = filmService.updateFilm(film);
        return new ResponseEntity<>(updatedFilm, OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteFilmById(@PathVariable("id") long filmID) {
        filmService.deleteFilmById(filmID);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Film>> getPopularFilms(
            @RequestParam(value = "count", defaultValue = "10") int count
    ) {
        List<Film> popularFilmsByCount = filmService.findPopularFilmsByCount(count);
        return new ResponseEntity<>(popularFilmsByCount, OK);
    }
}
