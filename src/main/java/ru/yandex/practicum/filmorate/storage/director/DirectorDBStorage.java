package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
public class DirectorDBStorage extends BaseRepository<Director> implements DirectorStorage {
    private static final String INSERT_QUERY = """
            INSERT INTO director (name) VALUES (?);
            """;
    private static final String INSERT_DIRECTOR_FOR_FILM = """
            INSERT INTO film_director (film_id, director_id)
            VALUES (?, ?);
            """;
    private static final String UPDATE_FILM_DIRECTOR = """
            UPDATE film_director
            SET film_id = ?,
            director_id = ?
            WHERE film_director_id = ?;
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT *
            FROM director
            WHERE director_id = ?;
            """;
    private static final String FIND_FILM_IDS_BY_DIRECTOR_ID_QUERY = """
            SELECT FD.FILM_ID
            FROM FILM_DIRECTOR FD
            WHERE DIRECTOR_ID = ?;
            """;
    private static final String FIND_ALL_QUERY = """
            SELECT *
            FROM director;
            """;
    private static final String UPDATE_QUERY = """
            UPDATE director
            SET name = ?
            WHERE director_id = ?;
            """;
    private static final String DELETE_QUERY = """
            DELETE FROM director WHERE director_id = ?;
            """;
    private static final String DELETE_DIRECTOR_OF_FILM_QUERY = """
            DELETE FROM FILM_DIRECTOR WHERE FILM_ID = ?;
            """;

    public DirectorDBStorage(JdbcTemplate jdbc, RowMapper<Director> rowMapper, ResultSetExtractor<List<Director>> extractor) {
        super(jdbc, rowMapper, extractor);
    }

    @Override
    public Director addDirector(Director director) {
        long id = insert(INSERT_QUERY, director.getName());
        director.setId(id);
        return director;
    }

    @Override
    public Director addDirectorForFilm(Long filmId, Director director) {
        insert(INSERT_DIRECTOR_FOR_FILM, filmId, director.getId());
        return director;
    }

    @Override
    public Optional<Director> findDirectorById(Long directorId) {
        return findOne(FIND_BY_ID_QUERY, directorId);
    }

    @Override
    public List<Long> findFilmIdsByDirectorId(Long directorId) {
        return jdbc.queryForList(FIND_FILM_IDS_BY_DIRECTOR_ID_QUERY, Long.class, directorId);
    }

    @Override
    public List<Director> findAllDirectors() {
        return findManyWithExtractor(FIND_ALL_QUERY);
    }

    @Override
    public Director updateDirector(Director director) {
        update(UPDATE_QUERY, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(Long directorId) {
        delete(DELETE_QUERY, directorId);
    }

    @Override
    public void deleteDirectorOfFilm(Long filmId) {
        delete(DELETE_DIRECTOR_OF_FILM_QUERY, filmId);
    }
}
