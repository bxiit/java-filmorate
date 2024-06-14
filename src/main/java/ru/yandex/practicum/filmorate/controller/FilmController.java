package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate EARLIEST_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    @PostMapping()
    public ResponseEntity<Film> addFilm(@RequestBody Film film) {
        validateFilm(film);

        film.setId(getMaxId());
        films.put(film.getId(), film);
        return new ResponseEntity<>(films.get(film.getId()), HttpStatus.CREATED);
    }

    @GetMapping()
    public ResponseEntity<List<Film>> getFilms() {
        return ResponseEntity.of(Optional.of(new ArrayList<>(films.values())));
    }

    @PutMapping()
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        validateFilm(film);

        Film filmToUpdate = Optional.ofNullable(films.get(film.getId()))
                .orElseThrow(() -> new NotFoundException("Не найден фильм с id: " + film.getId()));

        if (film.getDescription() != null) {
            filmToUpdate.setDescription(film.getDescription());
        }
        if (film.getName() != null) {
            filmToUpdate.setName(film.getName());
        }
        if (film.getDuration() != null) {
            filmToUpdate.setDuration(film.getDuration());
        }
        if (film.getReleaseDate() != null) {
            filmToUpdate.setReleaseDate(film.getReleaseDate());
        }
        films.put(filmToUpdate.getId(), filmToUpdate);
        return ResponseEntity.of(Optional.of(films.get(film.getId())));
    }

    private void validateFilm(Film film) {
        log.warn("Валидация фильма {}", film.getName());

        log.warn("Валидация имени {}", film.getName());
        if (film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        log.warn("Валидация описания {}", film.getDescription());
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может быть длиннее 200 символов");
        }

        log.warn("Валидация даты релиза {}", film.getReleaseDate());
        if (film.getReleaseDate().isBefore(EARLIEST_DATE)) {
            throw new ValidationException("Дата релиза фильма слишком старая");
        }

        log.warn("Валидация продолжительности {}", film.getDuration());
        if (film.getDuration().isNegative()) {
            throw new ValidationException("Продолжительность фильма не может быть ниже нуля");
        }

        log.info("Успешная валидация фильма {}", film.getName());
    }

    private Long getMaxId() {
        return IdGenerator.getMaxIdOfFilms(films.keySet());
    }
}
