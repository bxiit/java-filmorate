package ru.yandex.practicum.filmorate.storage.mappers.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class MpaExtractor implements ResultSetExtractor<List<Mpa>> {
    @Override
    public List<Mpa> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Mpa> mpas = new LinkedHashMap<>();
        long mpaId = rs.getLong("mpa_id");
        String name = rs.getString("name");
        Mpa mpa = new Mpa();
        mpa.setId(mpaId);
        mpa.setName(name);
        return new ArrayList<>(mpas.values());
    }
}
