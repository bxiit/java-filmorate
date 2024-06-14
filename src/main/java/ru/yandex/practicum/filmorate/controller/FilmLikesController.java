package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmLikesController {

    private final FilmService filmService;

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> likeFilm(@PathVariable("userId") long userID, @PathVariable("id") long filmID) {
        filmService.likeFilm(filmID, userID);
        return new ResponseEntity<>(OK);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<Void> unlikeFilm(@PathVariable("userId") long userID, @PathVariable("id") long filmID) {
        filmService.unlikeFilm(filmID, userID);
        return new ResponseEntity<>(OK);
    }
}
