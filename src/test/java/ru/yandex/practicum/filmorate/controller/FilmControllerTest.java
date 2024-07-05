package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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

        // Название фильма не может быть пустым
        // Описание фильма не может быть длиннее 200 символов

        Validator validator;
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }

        assertThat(validator)
                .isNotNull();

        Set<ConstraintViolation<NewFilmRequest>> validationResults = validator.validate(request);

        assertThat(validationResults)
                .hasSize(2)
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder(
                        "Название фильма не может быть пустым",
                        "Описание фильма не может быть длиннее 200 символов"
                );
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

        Validator validator;
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }

        assertThat(validator)
                .isNotNull();

        Set<ConstraintViolation<NewFilmRequest>> validationResults = validator.validate(request);
        assertThat(validationResults)
                .hasSize(1)
                .anySatisfy(violation -> assertThat(violation.getMessage())
                        .isEqualTo("Дата релиза фильма слишком старая"));
    }
}