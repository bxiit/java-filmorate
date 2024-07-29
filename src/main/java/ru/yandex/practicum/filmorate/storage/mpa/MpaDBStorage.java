package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@Primary
public class MpaDBStorage extends BaseRepository<Mpa> implements MpaStorage {

    private static final String FIND_ALL_QUERY = "SELECT * FROM MPA ORDER BY MPA_ID";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM MPA WHERE MPA_ID = ? ORDER BY MPA_ID";

    public MpaDBStorage(JdbcTemplate jdbc, RowMapper<Mpa> rowMapper, ResultSetExtractor<List<Mpa>> extractor) {
        super(jdbc, rowMapper, extractor);
    }

    @Override
    public Optional<Mpa> findMpaById(long mpaId) {
        return findOne(FIND_BY_ID_QUERY, mpaId);
    }

    @Override
    public List<Mpa> findAllMpa() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public void load(List<Film> films) {
        final Map<Long, Film> filmsById = films.stream().collect(Collectors.toMap(Film::getId, Function.identity()));
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        String sqlQuery = """
                                  select F.FILM_ID, M.MPA_ID, M.NAME
                                  from FILM F,
                                       MPA M
                                  where F.MPA_ID = M.MPA_ID
                                  and F.FILM_ID IN (""" + inSql + ");";
        jdbc.query(sqlQuery, (rs, rowNum) -> {
            final Film film = filmsById.get(rs.getLong("film_id"));
            Mpa mpa = rowMapper.mapRow(rs, 0);
            film.addMpa(mpa);
            return film;
        }, films.stream().map(Film::getId).toArray());
    }
}
