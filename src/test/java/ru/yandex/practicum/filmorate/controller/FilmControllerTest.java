package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FilmControllerTest extends BaseControllerTest<FilmController> {

    @Autowired
    public FilmControllerTest(FilmController controller) {
        super(controller);
    }

    @Test
    void testFilmModel_shouldThrowValidationException_invalidNameAndDescription() {
        // Название фильма не может быть пустым
        // Описание фильма не может быть длиннее 200 символов
        FilmDto request = FilmDto.builder()
                .name("")
                .description("film 1 name descriptionfilm 1 name descriptionfilm 1 name description" +
                             "film 1 name descriptionfilm 1 name descriptionfilm 1 name descriptionfilm" +
                             " 1 name descriptionfilm 1 name descriptionfilm 1 name description" +
                             "film 1 name descriptionfilm 1 name descriptionfilm 1 name descriptionfilm" +
                             " 1 name descriptionfilm 1 name descriptionfilm 1 name description" +
                             "film 1 name descriptionfilm 1 name descriptionfilm 1 name descriptionfilm" +
                             " 1 name descriptionfilm 1 name descriptionfilm 1 name description" +
                             "film 1 name descriptionfilm 1 name descriptionfilm 1 name descriptionfilm" +
                             " 1 name descriptionfilm 1 name descriptionfilm 1 name description" +
                             "film 1 name descriptionfilm 1 name descriptionfilm 1 name descriptionfilm" +
                             " 1 name descriptionfilm 1 name descriptionfilm 1 name description")
                .duration(Duration.ofMinutes(90))
                .releaseDate(LocalDate.now())
                .build();

        Validator validator;
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }

        assertThat(validator)
                .isNotNull();

        Set<ConstraintViolation<FilmDto>> validationResults = validator.validate(request);

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
        FilmDto request = FilmDto.builder()
                .name("Earliest film")
                .description("Description of Earliest film")
                .duration(Duration.ofMinutes(100))
                .releaseDate(LocalDate.of(1895, Month.DECEMBER, 28))
                .build();

        assertDoesNotThrow(() -> controller.addFilm(request));
    }

    @Test
    void testFilmModel_shouldThrowValidationException_releaseDateIsInvalid() {
        FilmDto request = FilmDto
                .builder()
                .name("Earliest film")
                .description("Description of Earliest film")
                .duration(Duration.ofMinutes(100))
                .releaseDate(LocalDate.of(1895, Month.DECEMBER, 27))
                .build();

        Validator validator;
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }

        assertThat(validator)
                .isNotNull();

        Set<ConstraintViolation<FilmDto>> validationResults = validator.validate(request);
        assertThat(validationResults)
                .hasSize(1)
                .anySatisfy(violation -> assertThat(violation.getMessage())
                        .isEqualTo("Дата релиза фильма слишком старая"));
    }

//    @Test
//    @Sql(value = "/sql/directors.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
//    void testFilmAdd_withDirector() throws Exception {
//        String postReqFilmWithDirector = """
//                {
//                    "name":"Режиссерское кино",
//                    "description":"Один режиссер с id = 1",
//                    "releaseDate":"2004-11-16",
//                    "duration": 115,
//                    "mpa":{
//                        "id":2
//                    },
//                    "genres":[{
//                        "id":2
//                    }],
//                    "directors": [{
//                        "id":1
//                    }]
//                }
//                """;
//        String resultOfPostReqFilmWithDirector = """
//                {
//                    "id": 1,
//                    "name":"Режиссерское кино",
//                    "description":"Один режиссер с id = 1",
//                    "releaseDate":"2004-11-16",
//                    "duration": 115,
//                    "mpa":{
//                        "id":2,
//                        "name": "PG"
//                    },
//                    "genres":[{
//                        "id":2,
//                        "name": "Драма"
//                    }],
//                    "directors": [{
//                        "id":1,
//                        "name": "Айсултан Сейтов"
//                    }]
//                }
//                """;
//        mockMvc.perform(
//                        post("films")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(postReqFilmWithDirector)
//                )
//                .andExpectAll(
//                        status().isCreated(),
//                        content().contentType(MediaType.APPLICATION_JSON),
//                        content().json(resultOfPostReqFilmWithDirector, true),
//                        jsonPath("$.directors.name").value("Айсултан Сейтов")
//                );
//    }
}