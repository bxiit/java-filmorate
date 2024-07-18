package ru.yandex.practicum.filmorate;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.controller.DirectorController;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.GenreController;
import ru.yandex.practicum.filmorate.controller.MpaController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.service.director.DirectorService;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.director.DirectorDBStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDBStorage;
import ru.yandex.practicum.filmorate.storage.mappers.RowMapperConfig;
import ru.yandex.practicum.filmorate.storage.mappers.extractors.ResultSetExtractorConfig;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDBStorage;
import ru.yandex.practicum.filmorate.storage.user.FriendDBStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDBStorage;

import java.util.Objects;

@JdbcTest
@ContextConfiguration(classes = {
        UserController.class, FilmController.class, DirectorController.class,
        MpaController.class, GenreController.class,
        UserService.class, FilmService.class, DirectorService.class,
        MpaService.class, GenreService.class,
        UserDBStorage.class, FriendDBStorage.class, FilmDBStorage.class, DirectorDBStorage.class,
        MpaDBStorage.class, GenreDBStorage.class,
        ResultSetExtractorConfig.class,
        RowMapperConfig.class
})
@AutoConfigureTestDatabase
@RequiredArgsConstructor
@ActiveProfiles("test")
public class FilmorateApplicationTests {

    public static final String SCHEMA_SQL = "schema.sql";
    public static final String DATA_SQL = "data.sql";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    private void postConstruct() throws Exception {
        Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
        Objects.requireNonNull(jdbcTemplate.getDataSource().getConnection());
    }

    @SneakyThrows({ NullPointerException.class})
    @AfterEach
    void clearStorage() {
        var schemaResource = new ClassPathResource(SCHEMA_SQL);
        var dataResource = new ClassPathResource(DATA_SQL);

        executeScript(schemaResource);
        executeScript(dataResource);
    }

    @SneakyThrows
    private void executeScript(ClassPathResource classPathResource) {
        ScriptUtils.executeSqlScript(
                jdbcTemplate.getDataSource().getConnection(),
                classPathResource
        );
    }
}
