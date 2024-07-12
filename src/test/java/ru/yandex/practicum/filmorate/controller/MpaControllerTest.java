package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MpaControllerTest extends BaseControllerTest<MpaController> {

    @Autowired
    public MpaControllerTest(MpaController controller) {
        super(controller);
    }

    @Test
    void testGetAllMpa() {
        List<Mpa> mpas = controller.getAllMpa();

        assertThat(mpas)
                .isNotNull()
                .hasSize(5);
    }

    @Test
    void testGetMpaById() {

        Mpa mpaById = controller.getMpaById(1);
        assertThat(mpaById)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "G");
    }
}