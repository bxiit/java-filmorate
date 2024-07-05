package ru.yandex.practicum.filmorate.storage.mappers.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class MpaExtractor implements ResultSetExtractor<List<Mpa>> {
    @Override
    public List<Mpa> extractData(ResultSet rs) throws SQLException, DataAccessException {
        return List.of();
    }
}
