package ru.yandex.practicum.filmorate.storage.mappers.extractors;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Primary
public class DirectorExtractor implements ResultSetExtractor<List<Director>> {

    @Override
    public List<Director> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Director> directors = new LinkedHashMap<>();
        while (rs.next()) {
            Director director = extractDirectorFromResultSet(rs);
            directors.put(director.getId(), director);
        }
        return new ArrayList<>(directors.values());
    }

    // Director Row Mapper тоже использует
    protected static Director extractDirectorFromResultSet(ResultSet rs) throws SQLException {
        long id = rs.getLong("director_id");
        return Director.builder()
                .id(id)
                .name(rs.getString("name"))
                .build();
    }
}
