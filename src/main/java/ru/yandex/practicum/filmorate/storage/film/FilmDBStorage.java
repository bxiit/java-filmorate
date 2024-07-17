package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("FilmDBStorage")
@Primary
public class FilmDBStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String FIND_ALL_QUERY = """
            select f.*,
                                      FG.GENRE_ID,
                                      FUL.USER_ID as liked_user_id
                               from FILM f
                               left join PUBLIC.FILM_GENRE FG on f.FILM_ID = FG.FILM_ID
                               left join PUBLIC.FILM_USER_LIKES FUL on f.FILM_ID = FUL.FILM_ID;
            """;
    private static final String FIND_POPULAR_FILM_IDS_QUERY = """
            select f.FILM_ID,
                   count(FUL.USER_ID) as likes
            from FILM f
            left join PUBLIC.FILM_USER_LIKES FUL on f.FILM_ID = FUL.FILM_ID
            group by f.FILM_ID
            order by likes desc
            limit ?;
            """;
    private static final String FIND_POPULAR_FILM_IDS_BY_GENRE_AND_YEAR_QUERY = """
            select f.FILM_ID,
                   count(FUL.USER_ID) as likes
            from (select fm.FILM_ID
                    from (select FILM.FILM_ID
                            from FILM
                            where EXTRACT(YEAR FROM CAST(FILM.RELEASE_DATE AS date)) = ?
                            ) as fm
                    join film_genre fg on fm.FILM_ID = fg.FILM_ID
                    where fg.genre_id = ?
                    ) as f
            left join PUBLIC.FILM_USER_LIKES FUL on f.FILM_ID = FUL.FILM_ID
            group by f.FILM_ID
            order by likes desc
            limit ?;
            """;
    private static final String FIND_POPULAR_FILM_IDS_BY_YEAR_QUERY = """
            select f.FILM_ID,
                   count(FUL.USER_ID) as likes
            from (select FILM.FILM_ID
                    from FILM
                    where EXTRACT(YEAR FROM CAST(FILM.RELEASE_DATE AS date)) = ?
                    ) as f
            left join PUBLIC.FILM_USER_LIKES FUL on f.FILM_ID = FUL.FILM_ID
            group by f.FILM_ID
            order by likes desc
            limit ?;
            """;
    private static final String FIND_POPULAR_FILM_IDS_BY_GENRE_QUERY = """
            select f.FILM_ID,
                   count(FUL.USER_ID) as likes
            from (select fm.FILM_ID
                    from FILM as fm
                    join film_genre fg on fm.FILM_ID = fg.FILM_ID
                    where fg.GENRE_ID = ?
                    ) as f
            left join PUBLIC.FILM_USER_LIKES FUL on f.FILM_ID = FUL.FILM_ID
            group by f.FILM_ID
            order by likes desc
            limit ?;
            """;
    private static final String FIND_BY_ID_QUERY = """
            select f.*,
                                      FG.GENRE_ID,
                                      FUL.USER_ID as liked_user_id
                               from FILM f
                               left join PUBLIC.FILM_GENRE FG on f.FILM_ID = FG.FILM_ID
                               left join PUBLIC.FILM_USER_LIKES FUL on f.FILM_ID = FUL.FILM_ID
                               where f.FILM_ID = ?;
            """;
    private static final String INSERT_QUERY = """
            INSERT INTO FILM (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
            VALUES ( ?, ?, ?, ?, ? );
            """;
    private static final String INSERT_GENRE_QUERY = """
            INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES ( ?, ? );
            """;
    private static final String UPDATE_QUERY = """
            UPDATE FILM
            SET NAME = ?,
            DESCRIPTION = ?,
            RELEASE_DATE = ?,
            DURATION = ?,
            MPA_ID = ?
            WHERE FILM_ID = ?;
            """;
    private static final String DELETE_FILM_GENRE_QUERY = """
            DELETE FROM FILM_GENRE
            WHERE FILM_ID = ?;
            """;
    private static final String DELETE_QUERY = "DELETE FROM FILM WHERE FILM_ID = ?";
    private static final String LIKE_FILM = """
            INSERT INTO FILM_USER_LIKES (FILM_ID, USER_ID)
            VALUES ( ?, ? );
            """;
    private static final String UNLIKE_FILM = """
            DELETE FROM FILM_USER_LIKES
            WHERE (FILM_ID = ? AND USER_ID = ?);
            """;

    public FilmDBStorage(JdbcTemplate jdbc, RowMapper<Film> rowMapper, ResultSetExtractor<List<Film>> extractor) {
        super(jdbc, rowMapper, extractor);
    }

    @Override
    public Film addFilm(Film film) {
        long filmId = 0;
        try {
            filmId = insert(
                    INSERT_QUERY,
                    film.getName(),
                    film.getDescription(),
                    film.getReleaseDate().format(DateTimeFormatter.ISO_DATE),
                    film.getDuration().toSeconds(),
                    film.getMpa() == null ? null : film.getMpa().getId()
            );
        } catch (DataIntegrityViolationException e) {
            handleAlreadyExistence("Фильм уже существует", e);
        }

        film.setId(filmId);
        for (GenreDto genreDto : film.getGenres()) {
            insertFilmGenre(filmId, genreDto.getId());
        }
        return film;
    }

    private void insertFilmGenre(long filmId, long genreId) {
        insert(INSERT_GENRE_QUERY, filmId, genreId);
    }

    @Override
    public Optional<Film> findFilmById(long filmId) {
        return findOneWithExtractor(FIND_BY_ID_QUERY, filmId);
    }

    @Override
    public List<Film> findAllFilms() {
        return findManyWithExtractor(FIND_ALL_QUERY);
    }

    public List<Film> findPopularFilms(long count) {
        List<Film> filmsByLikes = new ArrayList<>();
        SqlRowSet sqlRowSet = jdbc.queryForRowSet(FIND_POPULAR_FILM_IDS_QUERY, count);
        while (sqlRowSet.next()) {
            long filmId = sqlRowSet.getLong("film_id");
            Film film = findFilmById(filmId).orElseThrow(() -> new NotFoundException("Фильм не найден"));
            filmsByLikes.add(film);
        }
        return filmsByLikes;
    }

    @Override
    public List<Film> findPopularFilmsByGenreAndYear(int count, Long genreId, Integer year) {
        List<Film> filmsByLikes = new ArrayList<>();
        SqlRowSet sqlRowSet;

        if (year != null ) {
            if (genreId != null) {
                sqlRowSet = jdbc.queryForRowSet(FIND_POPULAR_FILM_IDS_BY_GENRE_AND_YEAR_QUERY, year, genreId, count);
            } else {
                sqlRowSet = jdbc.queryForRowSet(FIND_POPULAR_FILM_IDS_BY_YEAR_QUERY, year, count);
            }
        } else {
            if (genreId != null) {
                sqlRowSet = jdbc.queryForRowSet(FIND_POPULAR_FILM_IDS_BY_GENRE_QUERY, genreId, count);
            } else {
                return findPopularFilms(count);
            }
        }

        while (sqlRowSet.next()) {
            long filmId = sqlRowSet.getLong("film_id");
            Film film = findFilmById(filmId).orElseThrow(() -> new NotFoundException("Фильм не найден"));
            filmsByLikes.add(film);
        }
        return filmsByLikes;
    }

    @Override
    public Film updateFilm(Film film) {
        // полное удаление всех жанров фильма
        deleteFilmGenre(film.getId());

        // само обновление
        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate().format(DateTimeFormatter.ISO_DATE),
                film.getDuration().toSeconds(),
                film.getMpa().getId(),
                film.getId()
        );

        // инсерт жанров которые есть у обновляемого фильма
        for (GenreDto genreDto : film.getGenres()) {
            insertFilmGenre(film.getId(), genreDto.getId());
        }

        return film;
    }

    private boolean deleteFilmGenre(long filmId) {
        return delete(DELETE_FILM_GENRE_QUERY, filmId);
    }

    @Override
    public boolean deleteFilmById(long filmId) {
        return delete(DELETE_QUERY, filmId);
    }

    @Override
    public boolean likeFilm(long filmId, long userId) {
        return insert(LIKE_FILM, filmId, userId) != 0;
    }

    @Override
    public boolean unlikeFilm(long filmId, long userId) {
        return delete(UNLIKE_FILM, filmId, userId);
    }
}
