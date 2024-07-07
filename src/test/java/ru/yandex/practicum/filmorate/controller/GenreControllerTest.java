package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GenreControllerTest extends BaseControllerTest<GenreController> {

    @Autowired
    public GenreControllerTest(GenreController controller) {
        super(controller);
    }

    @Test
    void findAllGenre() {
        List<Genre> genres = controller.findAllGenre();
        assertThat(genres)
                .hasSize(6);
    }

    @Test
    void findGenreById() {
        Genre thrillerGenre = controller.findGenreById(4);

        assertThat(thrillerGenre)
                .hasFieldOrPropertyWithValue("id", 4L)
                .hasFieldOrPropertyWithValue("name", "Триллер");
    }
}