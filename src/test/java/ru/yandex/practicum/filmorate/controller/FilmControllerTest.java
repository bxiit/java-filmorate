package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    FilmController filmController = new FilmController();

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
                LocalDate.now(), Duration.ofMinutes(90));

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }

    @Test
    void testFilmModel_shouldNotThrowValidationException_releaseDateIsExactlyTheEarliestDate() {
        Film film = new Film(null, "Earliest film", "Description of Earliest film",
                LocalDate.of(1895, Month.DECEMBER, 28), Duration.ofMinutes(100));

        assertDoesNotThrow(() -> filmController.addFilm(film));
    }

    @Test
    void testFilmModel_shouldThrowValidationException_releaseDateIsInvalid() {
        Film film = new Film(null, "Earliest film", "Description of Earliest film",
                LocalDate.of(1895, Month.DECEMBER, 27), Duration.ofMinutes(100));

        assertThrows(ValidationException.class, () -> filmController.addFilm(film));
    }
}