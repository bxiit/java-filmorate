package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.mappers.extractors.DirectorExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DirectorRowMapper extends DirectorExtractor implements RowMapper<Director> {
    @Override
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        // наследуется от DirectorExtractor для использования метода extractDirectorFromResultSet
        return extractDirectorFromResultSet(rs);
    }
}
