package ru.yandex.practicum.filmorate.storage.mappers.extractors;

import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;


public class FilmWithDirectorExtractor extends FilmsExtractor {
    @Override
    protected void setDirectory(ResultSet rs, Film film) throws SQLException {
        long directoryId = rs.getLong("director_id");
        if (directoryId == 0) {
            return;
        }
        if (film.getDirectors() == null) {
            film.setDirectors(new HashSet<>());
        }
        DirectorDto directorDto = DirectorDto.builder()
                .id(directoryId)
                .build();
        film.getDirectors().add(directorDto);
    }
}
