package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    static FilmController filmController;

    @BeforeAll
    static void setUp() {
        FilmService filmService = new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage());
        filmController = new FilmController(filmService);
    }

    @Test
    void testFilmModel_shouldThrowValidationException_invalidNameAndDescription() {
        Film film = new Film(null, "",
                "film 1 name descriptionfilm 1 name descriptionfilm 1 name description" +
                "film 1 name descriptionfilm 1 name descriptionfilm 1 name descriptionfilm" +
                " 1 name descriptionfilm 1 name descriptionfilm 1 name description" +
                "film 1 name descriptionfilm 1 name descriptionfilm 1 name descriptionfilm" +
                " 1 name descriptionfilm 1 name descriptionfilm 1 name description" +
                "film 1 name descriptionfilm 1 name descriptionfilm 1 name descriptionfilm" +
                " 1 name descriptionfilm 1 name descriptionfilm 1 name description" +
                "film 1 name descriptionfilm 1 name descriptionfilm 1 name descriptionfilm" +
                " 1 name descriptionfilm 1 name descriptionfilm 1 name description" +
                "film 1 name descriptionfilm 1 name descriptionfilm 1 name descriptionfilm" +
                " 1 name descriptionfilm 1 name descriptionfilm 1 name description",
                LocalDate.now(), Duration.ofMinutes(90), new HashSet<>());

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void testFilmModel_shouldNotThrowValidationException_releaseDateIsExactlyTheEarliestDate() {
        Film film = new Film(null, "Earliest film", "Description of Earliest film",
                LocalDate.of(1895, Month.DECEMBER, 28), Duration.ofMinutes(100), new HashSet<>());

        assertDoesNotThrow(() -> filmController.addFilm(film));
    }

    @Test
    void testFilmModel_shouldThrowValidationException_releaseDateIsInvalid() {
        Film film = new Film(null, "Earliest film", "Description of Earliest film",
                LocalDate.of(1895, Month.DECEMBER, 27), Duration.ofMinutes(100), new HashSet<>());

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }
}