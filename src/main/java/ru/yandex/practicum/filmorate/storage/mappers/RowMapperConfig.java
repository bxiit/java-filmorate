package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

@Configuration
public class RowMapperConfig {
    @Bean
    public RowMapper<User> userRowMapper() {
        return new UserRowMapper();
    }

    @Bean
    public RowMapper<Film> filmRowMapper() {
        return new FilmRowMapper();
    }

    @Bean
    public RowMapper<Mpa> mpaRowMapper() {
        return new MpaRowMapper();
    }

    @Bean
    public RowMapper<Genre> genreRowMapper() {
        return new GenreRowMapper();
    }

    @Bean
    public RowMapper<Director> directorRowMapper() {
        return new DirectorRowMapper();
    }
}
