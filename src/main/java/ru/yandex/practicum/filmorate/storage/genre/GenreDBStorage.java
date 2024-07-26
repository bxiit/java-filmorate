package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class GenreDBStorage extends BaseRepository<Genre> implements GenreStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM GENRE ORDER BY GENRE_ID";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM GENRE WHERE GENRE_ID = ? ORDER BY GENRE_ID";
    private static final String COMMA = ",";

    public GenreDBStorage(JdbcTemplate jdbc, RowMapper<Genre> rowMapper, ResultSetExtractor<List<Genre>> extractor) {
        super(jdbc, rowMapper, extractor);
    }

    @Override
    public Optional<Genre> findGenreById(long genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

    @Override
    public List<Genre> findAllGenres() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public void load(List<Film> films) {
        final Map<Long, Film> filmsById = films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity()));
        String inSql = String.join(COMMA, Collections.nCopies(films.size(), "?"));
        final String sqlQuery = """
                                        select G.GENRE_ID,
                                               G.NAME,
                                               FG.FILM_ID
                                        from GENRE G,
                                             FILM_GENRE FG
                                        WHERE FG.GENRE_ID = G.GENRE_ID
                                          AND FG.FILM_ID IN (""" + inSql + ");";
        log.debug("Выполнение запроса {} для загрузки имени жанра фильмов", sqlQuery);
        jdbc.query(sqlQuery, (rs, rowNum) -> {
            //Получили из ResultSet'a идентификатор фильма и извлекли по нему из мапы значение)
            final Film film = filmsById.get(rs.getLong("FILM_ID"));
            //Добавили в коллекцию внутри объекта класса FIlm новый жанр)
            Genre genre = rowMapper.mapRow(rs, rowNum);
            film.addGenre(genre);
            //Преобразуем коллекцию типа Film к Integer и в массив, так как передавать требуется именно его
            return film;
        }, films.stream().map(Film::getId).toArray());
    }
}
