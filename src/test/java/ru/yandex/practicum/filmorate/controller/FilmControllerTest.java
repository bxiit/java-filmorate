package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class FilmControllerTest extends BaseControllerTests<FilmController> {

    @Autowired
    public FilmControllerTest(FilmController controller) {
        super(controller);
    }

    @Test
    void testFilmModel_shouldThrowValidationException_invalidNameAndDescription() {
        NewFilmRequest request = new NewFilmRequest();
        request.setName("");
        request.setDescription("film 1 name descriptionfilm 1 name descriptionfilm 1 name description" +
                               "film 1 name descriptionfilm 1 name descriptionfilm 1 name descriptionfilm" +
                               " 1 name descriptionfilm 1 name descriptionfilm 1 name description" +
                               "film 1 name descriptionfilm 1 name descriptionfilm 1 name descriptionfilm" +
                               " 1 name descriptionfilm 1 name descriptionfilm 1 name description" +
                               "film 1 name descriptionfilm 1 name descriptionfilm 1 name descriptionfilm" +
                               " 1 name descriptionfilm 1 name descriptionfilm 1 name description" +
                               "film 1 name descriptionfilm 1 name descriptionfilm 1 name descriptionfilm" +
                               " 1 name descriptionfilm 1 name descriptionfilm 1 name description" +
                               "film 1 name descriptionfilm 1 name descriptionfilm 1 name descriptionfilm" +
                               " 1 name descriptionfilm 1 name descriptionfilm 1 name description");
        request.setDuration(Duration.ofMinutes(90));
        request.setReleaseDate(LocalDate.now());

        assertThrows(ValidationException.class, () -> controller.addFilm(request));
    }

    @Test
    void testFilmModel_shouldNotThrowValidationException_releaseDateIsExactlyTheEarliestDate() {
        NewFilmRequest request = new NewFilmRequest();
        request.setName("Earliest film");
        request.setDescription("Description of Earliest film");
        request.setDuration(Duration.ofMinutes(100));
        request.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 28));

        assertDoesNotThrow(() -> controller.addFilm(request));
    }

    @Test
    void testFilmModel_shouldThrowValidationException_releaseDateIsInvalid() {
        NewFilmRequest request = new NewFilmRequest();
        request.setName("Earliest film");
        request.setDescription("Description of Earliest film");
        request.setDuration(Duration.ofMinutes(100));
        request.setReleaseDate(LocalDate.of(1895, Month.DECEMBER, 27));

        assertThrows(ValidationException.class, () -> controller.addFilm(request));
    }
}