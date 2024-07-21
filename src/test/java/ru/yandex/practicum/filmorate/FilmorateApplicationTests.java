package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.controller.MpaController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.service.event.EventService;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.film.FilmServiceFacade;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.service.user.UserServiceFacade;
import ru.yandex.practicum.filmorate.storage.event.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDBStorage;
import ru.yandex.practicum.filmorate.storage.mappers.RowMapperConfig;
import ru.yandex.practicum.filmorate.storage.mappers.extractors.ResultSetExtractorConfig;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDBStorage;
import ru.yandex.practicum.filmorate.storage.user.FriendDBStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDBStorage;

import java.sql.SQLException;
import java.util.Objects;

@JdbcTest
@ContextConfiguration(classes = {
        UserController.class, FilmController.class,
        MpaController.class, GenreController.class,
        UserServiceFacade.class, FilmServiceFacade.class,
        EventService.class,
        UserService.class, FilmService.class,
        MpaService.class, GenreService.class,
        UserDBStorage.class, FriendDBStorage.class, FilmDBStorage.class,
        MpaDBStorage.class, GenreDBStorage.class, EventDbStorage.class,
        ResultSetExtractorConfig.class,
        RowMapperConfig.class
})
@AutoConfigureTestDatabase
@RequiredArgsConstructor
@ActiveProfiles("test")
public class FilmorateApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @SneakyThrows({SQLException.class, NullPointerException.class})
    @BeforeEach
    void clearStorage() {
        ClassPathResource schemaResource = new ClassPathResource("schema.sql");
        ClassPathResource dataResource = new ClassPathResource("data.sql");
        Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();

        ScriptUtils.executeSqlScript(
                jdbcTemplate.getDataSource().getConnection(),
                schemaResource);
        ScriptUtils.executeSqlScript(jdbcTemplate.getDataSource().getConnection(),
                dataResource);
    }
}
