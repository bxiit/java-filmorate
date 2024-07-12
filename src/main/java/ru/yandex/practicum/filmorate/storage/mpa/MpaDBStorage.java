package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class MpaDBStorage extends BaseRepository<Mpa> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM MPA ORDER BY MPA_ID";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM MPA WHERE MPA_ID = ? ORDER BY MPA_ID";

    public MpaDBStorage(JdbcTemplate jdbc, RowMapper<Mpa> rowMapper, ResultSetExtractor<List<Mpa>> extractor) {
        super(jdbc, rowMapper, extractor);
    }

    public Optional<Mpa> findMpaById(long mpaId) {
        return findOne(FIND_BY_ID_QUERY, mpaId);
    }

    public List<Mpa> findAllMpa() {
        return findMany(FIND_ALL_QUERY);
    }
}
