package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.ConflictException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> rowMapper;
    protected final ResultSetExtractor<List<T>> extractor;

    protected long insert(String query, Object... params) {
        log.debug("Сохранение по запросу: {} с параметрами {}", query, params);

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, rowMapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected Optional<T> findOneWithExtractor(String query, Object... params) {
        try {
            List<T> result = jdbc.query(query, extractor, params);
            if (result == null || result.isEmpty()) {
                return Optional.empty();
            }
            return Optional.ofNullable(result.getFirst());
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, rowMapper, params);
    }

    protected List<T> findManyWithExtractor(String query, Object... params) {
        return jdbc.query(query, extractor, params);
    }

    protected boolean update(String query, Object... params) {
        log.debug("Обновление записи по запросу: {} с параметрами {}", query, params);
        int updatedRows = jdbc.update(query, params);
        return updatedRows > 0;
    }

    protected boolean delete(String query, Object... params) {
        int deletedRows = jdbc.update(query, params);
        return deletedRows > 0;
    }

    protected void handleAlreadyExistence(String message, DataIntegrityViolationException e) {
        if (e.getCause() instanceof JdbcSQLIntegrityConstraintViolationException) {
            JdbcSQLIntegrityConstraintViolationException constraintException =
                    (JdbcSQLIntegrityConstraintViolationException) e.getCause();
            if (constraintException.getErrorCode() == 23505) {
                throw new ConflictException(message);
            }
        }
    }

    protected void noPkInsert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);
    }
}
