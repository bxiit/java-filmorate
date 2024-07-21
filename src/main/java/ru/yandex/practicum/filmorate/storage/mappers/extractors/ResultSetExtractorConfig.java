package ru.yandex.practicum.filmorate.storage.mappers.extractors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.Event;

import java.util.List;

@Configuration
public class ResultSetExtractorConfig {
    @Bean
    public ResultSetExtractor<List<User>> userResultSetExtractor() {
        return new UsersExtractor();
    }

    @Bean
    public ResultSetExtractor<List<Film>> filmResultSetExtractor() {
        return new FilmsExtractor();
    }

    @Bean
    public ResultSetExtractor<List<Mpa>> mpaResultSetExtractor() {
        return new MpaExtractor();
    }

    @Bean
    public ResultSetExtractor<List<Genre>> genreResultSetExtractor() {
        return new GenreExtractor();
    }

    @Bean
    public ResultSetExtractor<List<Review>> reviewResultSetExtractor() {
        return new ReviewExtractor();
    }

    @Bean
    public ResultSetExtractor<List<Event>> eventResultSetExtractor() {
        return new EventExtractor();
    }
}
