package ru.yandex.practicum.filmorate.storage.mappers.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class FilmsExtractor implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        final Map<Long, Film> films = new LinkedHashMap<>();

        while (rs.next()) {
            long filmId = rs.getLong("film_id");

            if (films.containsKey(filmId)) {
                Film film = films.get(filmId);
                // Добавление жанров
                setGenre(rs, film);
                // Добавление лайков (айди юзеров)
                setLike(rs, film);
                // Добавление режиссера
                setDirectory(rs, film);
                continue;
            }
            long durationSeconds = rs.getLong("duration");
            Film film = Film
                    .builder()
                    .id(filmId)
                    .name(rs.getString("name"))
                    .description(rs.getString("description"))
                    .releaseDate(rs.getDate("release_date").toLocalDate())
                    .duration(
                            Duration.ofSeconds(durationSeconds)
                    )
                    .build();
            setLike(rs, film);
            setMpa(rs, film);
            setGenre(rs, film);
            setDirectory(rs, film);
            films.put(film.getId(), film);
        }
        return new ArrayList<>(films.values());
    }

    protected void setMpa(ResultSet rs, Film film) throws SQLException {
        long mpaId = rs.getLong("mpa_id");
        if (mpaId == 0) {
            return;
        }
        MpaDto mpaDto = MpaDto.builder()
                .id(mpaId)
                .build();
        film.setMpa(mpaDto);
    }

    protected void setLike(ResultSet rs, Film film) throws SQLException {
        long likedUserId = rs.getLong("liked_user_id");
        if (likedUserId == 0) {
            return;
        }
        if (film.getLikedUsersIDs() == null) {
            film.setLikedUsersIDs(new HashSet<>());
        }
        film.getLikedUsersIDs().add(likedUserId);
    }

    protected void setGenre(ResultSet rs, Film film) throws SQLException {
        long genreId = rs.getLong("genre_id");
        if (genreId == 0) {
            return;
        }
        if (film.getGenres() == null) {
            film.setGenres(new TreeSet<>());
        }
        GenreDto genreDto = GenreDto.builder()
                .id(genreId)
                .build();
        film.getGenres().add(genreDto);
    }

    protected void setDirectory(ResultSet rs, Film film) throws SQLException {
    }
}
