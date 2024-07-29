package ru.yandex.practicum.filmorate.storage.mappers.extractors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GenreExtractor implements ResultSetExtractor<List<Genre>> {
    @Override
    public List<Genre> extractData(ResultSet rs) throws SQLException, DataAccessException {
        log.debug("Преобразование в объект Genre");
        Map<Long, Genre> genres = new LinkedHashMap<>();

        while (rs.next()) {
            long genreId = rs.getLong("genre_id");
            String name = rs.getString("name");

            Genre genre = new Genre();
            genre.setId(genreId);
            genre.setName(name);
            genres.put(genreId, genre);

            log.debug("Объект Genre {}", genre);
        }
        return new ArrayList<>(genres.values());
    }
}
