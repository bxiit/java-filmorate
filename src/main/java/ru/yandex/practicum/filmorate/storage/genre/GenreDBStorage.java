package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDBStorage extends BaseRepository<Genre> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM GENRE ORDER BY GENRE_ID";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM GENRE WHERE GENRE_ID = ? ORDER BY GENRE_ID";

    public GenreDBStorage(JdbcTemplate jdbc, RowMapper<Genre> rowMapper, ResultSetExtractor<List<Genre>> extractor) {
        super(jdbc, rowMapper, extractor);
    }

    public Optional<Genre> findGenreById(long genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

    public List<Genre> findAllGenres() {
        return findMany(FIND_ALL_QUERY);
    }
}
