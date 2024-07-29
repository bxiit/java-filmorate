package ru.yandex.practicum.filmorate;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
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
import ru.yandex.practicum.filmorate.service.director.DirectorServiceImpl;
import ru.yandex.practicum.filmorate.service.event.EventServiceImpl;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.film.FilmServiceFacade;
import ru.yandex.practicum.filmorate.service.film.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.genre.GenreService;
import ru.yandex.practicum.filmorate.service.genre.GenreServiceImpl;
import ru.yandex.practicum.filmorate.service.mpa.MpaService;
import ru.yandex.practicum.filmorate.service.mpa.MpaServiceImpl;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.service.user.UserServiceFacade;
import ru.yandex.practicum.filmorate.service.user.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.director.DirectorDBStorage;
import ru.yandex.practicum.filmorate.storage.event.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.friend.FriendDBStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDBStorage;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.extractors.DirectorExtractor;
import ru.yandex.practicum.filmorate.storage.mappers.extractors.EventExtractor;
import ru.yandex.practicum.filmorate.storage.mappers.extractors.FilmsExtractor;
import ru.yandex.practicum.filmorate.storage.mappers.extractors.GenreExtractor;
import ru.yandex.practicum.filmorate.storage.mappers.extractors.MpaExtractor;
import ru.yandex.practicum.filmorate.storage.mappers.extractors.ReviewExtractor;
import ru.yandex.practicum.filmorate.storage.mappers.extractors.UsersExtractor;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDBStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDBStorage;

import java.util.Objects;

@JdbcTest
@ContextConfiguration(classes = {
        UserController.class, FilmController.class, DirectorController.class, MpaController.class, GenreController.class,
        UserServiceFacade.class, FilmServiceFacade.class,
        UserServiceImpl.class, FilmServiceImpl.class, DirectorServiceImpl.class,
        MpaServiceImpl.class, GenreServiceImpl.class, EventServiceImpl.class,
        UserDBStorage.class, FriendDBStorage.class, FilmDBStorage.class, DirectorDBStorage.class,
        UserService.class, FilmService.class, DirectorService.class, MpaService.class, GenreService.class,
        UserDBStorage.class, FriendDBStorage.class, FilmDBStorage.class, DirectorDBStorage.class,
        MpaDBStorage.class, GenreDBStorage.class, EventDbStorage.class,
        UserRowMapper.class, FilmRowMapper.class, GenreRowMapper.class, MpaRowMapper.class, EventRowMapper.class,
        DirectorRowMapper.class, ReviewRowMapper.class,
        DirectorExtractor.class, EventExtractor.class, FilmsExtractor.class, GenreExtractor.class,
        MpaExtractor.class, ReviewExtractor.class, UsersExtractor.class
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

    @SneakyThrows({NullPointerException.class})
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
