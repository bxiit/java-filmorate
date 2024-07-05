package ru.yandex.practicum.filmorate.storage.mappers.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class FilmsExtractor implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        final Map<Long, Film> films = new LinkedHashMap<>();

        while (rs.next()) {
            long filmId = rs.getLong("film_id");

            if (films.containsKey(filmId)) {
                Film film = films.get(filmId);
                // Добавление жанров
                addGenre(rs, film);
                // Добавление лайков (айди юзеров)
                addLike(rs, film);
                continue;
            }
            Film film = new Film();
            film.setId(rs.getLong("film_id"));
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            long durationSeconds = rs.getLong("duration");
            film.setDuration(Duration.ofSeconds(durationSeconds));
            addLike(rs, film);
            setMpa(rs, film);
            addGenre(rs, film);
            films.put(film.getId(), film);
        }
        return new ArrayList<>(films.values());
    }

    private void setMpa(ResultSet rs, Film film) throws SQLException {
        long mpaId = rs.getLong("mpa_id");
        if (mpaId == 0) {
            return;
        }
        MpaDto mpaDto = new MpaDto();
        mpaDto.setId(mpaId);
        film.setMpa(mpaDto);
    }

    private void addLike(ResultSet rs, Film film) throws SQLException {
        long likedUserId = rs.getLong("liked_user_id");
        if (likedUserId == 0) {
            return;
        }
        film.getLikedUsersIDs().add(likedUserId);
    }

    private void addGenre(ResultSet rs, Film film) throws SQLException {
        long genreId = rs.getLong("genre_id");
        if (genreId == 0) {
            return;
        }
        GenreDto genreDto = new GenreDto();
        genreDto.setId(genreId);
        film.getGenres().add(genreDto);
    }
}
