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
@Qualifier("filmDBStorage")
@Primary
public class FilmDBStorage extends BaseRepository<Film> implements FilmStorage {

    private static final String FIND_ALL_QUERY = """
            select f.*,
            FG.GENRE_ID,
            FUL.USER_ID as liked_user_id,
            FD.DIRECTOR_ID
            from FILM f
            left join PUBLIC.FILM_GENRE FG on f.FILM_ID = FG.FILM_ID
            left join PUBLIC.FILM_USER_LIKES FUL on f.FILM_ID = FUL.FILM_ID
            left join PUBLIC.FILM_DIRECTOR FD on f.FILM_ID = FD.FILM_ID;
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
            select *,
            FUL.USER_ID as liked_user_id,
            FD.DIRECTOR_ID
            from FILM f
            left join PUBLIC.FILM_GENRE FG on f.FILM_ID = FG.FILM_ID
            left join PUBLIC.FILM_USER_LIKES FUL on f.FILM_ID = FUL.FILM_ID
            join (select FUL.FILM_ID, count(FUL.USER_ID) as count
                  from PUBLIC.FILM_USER_LIKES FUL
             	  group by FUL.FILM_ID) as likes ON f.FILM_ID = likes.FILM_ID
            left join PUBLIC.FILM_DIRECTOR FD on f.FILM_ID = FD.FILM_ID
            where EXTRACT(YEAR FROM CAST(f.RELEASE_DATE AS date)) = ?
            and GENRE_ID = ?
            order by likes.count desc
            limit ?
            """;
    private static final String FIND_POPULAR_FILM_IDS_BY_YEAR_QUERY = """
            select *,
            FUL.USER_ID as liked_user_id,
            FD.DIRECTOR_ID
            from FILM f
            left join PUBLIC.FILM_GENRE FG on f.FILM_ID = FG.FILM_ID
            left join PUBLIC.FILM_USER_LIKES FUL on f.FILM_ID = FUL.FILM_ID
            join (select FUL.FILM_ID, count(FUL.USER_ID) as count
                  from PUBLIC.FILM_USER_LIKES FUL
             	  group by FUL.FILM_ID) as likes ON f.FILM_ID = likes.FILM_ID
            left join PUBLIC.FILM_DIRECTOR FD on f.FILM_ID = FD.FILM_ID
            where EXTRACT(YEAR FROM CAST(f.RELEASE_DATE AS date)) = ?
            order by likes.count desc
            limit ?
            """;
    private static final String FIND_POPULAR_FILM_IDS_BY_GENRE_QUERY = """
            select *,
            FUL.USER_ID as liked_user_id,
            FD.DIRECTOR_ID
            from FILM f
            left join PUBLIC.FILM_GENRE FG on f.FILM_ID = FG.FILM_ID
            left join PUBLIC.FILM_USER_LIKES FUL on f.FILM_ID = FUL.FILM_ID
            join (select FUL.FILM_ID, count(FUL.USER_ID) as count
                  from PUBLIC.FILM_USER_LIKES FUL
             	  group by FUL.FILM_ID) as likes ON f.FILM_ID = likes.FILM_ID
            left join PUBLIC.FILM_DIRECTOR FD on f.FILM_ID = FD.FILM_ID
            where GENRE_ID = ?
            order by likes.count desc
            limit ?
            """;
    private static final String FIND_BY_ID_QUERY = """
            select f.*,
            FG.GENRE_ID,
            FUL.USER_ID as liked_user_id,
            FD.DIRECTOR_ID
            from FILM f
            left join PUBLIC.FILM_GENRE FG on f.FILM_ID = FG.FILM_ID
            left join PUBLIC.FILM_USER_LIKES FUL on f.FILM_ID = FUL.FILM_ID
            left join PUBLIC.FILM_DIRECTOR FD on f.FILM_ID = FD.FILM_ID
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
    private static final String FIND_RECOMMENDED_FILMS = """
            SELECT REC_FILMS.FILM_ID AS film_id,
                   F.NAME AS name,
                   F.DESCRIPTION AS description,
                   F.RELEASE_DATE AS release_date,
                   F.DURATION AS duration,
                   F.MPA_ID AS mpa_id,
                   FG.GENRE_ID AS genre_id,
                   FUL.USER_ID as liked_user_id,
                   FD.DIRECTOR_ID
            FROM (
                SELECT FILM_ID
                FROM FILM_USER_LIKES
                WHERE FILM_ID IN (
                    SELECT FILM_ID
                    FROM FILM_USER_LIKES
                    WHERE USER_ID = (
                        SELECT USER_ID
                        FROM FILM_USER_LIKES
                        WHERE FILM_ID IN (
                              SELECT FILM_ID
                              FROM FILM_USER_LIKES
                              WHERE USER_ID = ?)
                        AND USER_ID != ?
                        GROUP BY USER_ID
                        ORDER BY COUNT(FILM_ID) DESC
                        LIMIT 1))
                AND FILM_ID NOT IN (
                    SELECT USER_FILMS.FILM_ID
                    FROM FILM_USER_LIKES AS USER_FILMS
                    WHERE USER_FILMS.USER_ID = ?)) AS REC_FILMS
            LEFT JOIN FILM AS F ON REC_FILMS.FILM_ID = F.FILM_ID
            LEFT JOIN FILM_GENRE FG on REC_FILMS.FILM_ID = FG.FILM_ID
            LEFT JOIN FILM_USER_LIKES FUL on REC_FILMS.FILM_ID = FUL.FILM_ID
            LEFT JOIN PUBLIC.FILM_DIRECTOR FD ON F.FILM_ID = FD.FILM_ID;
            """;
    private static final String COMMON_FILMS_ORDERED_POPULARITY = """
            SELECT F.FILM_ID AS film_id,
                   F.NAME AS name,
                   F.DESCRIPTION AS description,
                   F.RELEASE_DATE AS release_date,
                   F.DURATION AS duration,
                   F.MPA_ID AS mpa_id,
                   FG.GENRE_ID AS genre_id,
                   FUL.USER_ID as liked_user_id,
                   FD.DIRECTOR_ID
            FROM (
                SELECT COMMON_FILMS.FILM_ID
                FROM (
                    SELECT FILM_ID
                    FROM FILM_USER_LIKES
                    WHERE USER_ID = ?
                    AND FILM_ID IN (
                        SELECT FILM_ID
                        FROM FILM_USER_LIKES
                        WHERE USER_ID = ?)
                    ) AS COMMON_FILMS
                LEFT JOIN (
                    SELECT FILM_ID,
                           COUNT(USER_ID) AS USER_LIKES
                    FROM FILM_USER_LIKES
                    GROUP BY FILM_ID) AS POPULARITY ON COMMON_FILMS.FILM_ID = POPULARITY.FILM_ID
                ORDER BY POPULARITY.USER_LIKES DESC
                ) AS SORTED_COMMON_FILMS
            LEFT JOIN FILM AS F ON SORTED_COMMON_FILMS.FILM_ID = F.FILM_ID
            LEFT JOIN FILM_GENRE FG on SORTED_COMMON_FILMS.FILM_ID = FG.FILM_ID
            LEFT JOIN FILM_USER_LIKES FUL on SORTED_COMMON_FILMS.FILM_ID = FUL.FILM_ID
            LEFT JOIN PUBLIC.FILM_DIRECTOR FD ON F.FILM_ID = FD.FILM_ID;
            """;

    private static final String FIND_FILMS_BY_FILM_TITLE_QUERY = """
            SELECT F.*,
            FG.GENRE_ID,
            FUL.USER_ID as liked_user_id,
            FD.DIRECTOR_ID
            FROM FILM F
            LEFT JOIN PUBLIC.FILM_GENRE FG ON F.FILM_ID = FG.FILM_ID
            LEFT JOIN PUBLIC.FILM_USER_LIKES FUL ON F.FILM_ID = FUL.FILM_ID
            LEFT JOIN PUBLIC.FILM_DIRECTOR FD ON F.FILM_ID = FD.FILM_ID
            WHERE F.NAME ILIKE ?
            """;

    private static final String FIND_FILMS_BY_DIRECTOR_NAME_QUERY = """
            SELECT F.*,
            FG.GENRE_ID,
            FUL.USER_ID as liked_user_id,
            FD.DIRECTOR_ID
            FROM FILM F
            LEFT JOIN PUBLIC.FILM_GENRE FG ON F.FILM_ID = FG.FILM_ID
            LEFT JOIN PUBLIC.FILM_USER_LIKES FUL ON F.FILM_ID = FUL.FILM_ID
            LEFT JOIN PUBLIC.FILM_DIRECTOR FD ON F.FILM_ID = FD.FILM_ID
            LEFT JOIN PUBLIC.DIRECTOR D ON FD.DIRECTOR_ID = D.DIRECTOR_ID
            WHERE D.NAME ILIKE ?
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

        if (year != null) {
            if (genreId != null) {
                return findManyWithExtractor(FIND_POPULAR_FILM_IDS_BY_GENRE_AND_YEAR_QUERY, year, genreId, count);
            } else {
                return findManyWithExtractor(FIND_POPULAR_FILM_IDS_BY_YEAR_QUERY, year, count);
            }
        } else {
            if (genreId != null) {
                return findManyWithExtractor(FIND_POPULAR_FILM_IDS_BY_GENRE_QUERY, genreId, count);
            } else {
                return findPopularFilms(count);
            }
        }
    }

    @Override
    public List<Film> findFilmsByQueryFilmTitle(String search) {
        return findManyWithExtractor(FIND_FILMS_BY_FILM_TITLE_QUERY, search);
    }

    @Override
    public List<Film> findFilmsByQueryDirectorName(String search) {
        return findManyWithExtractor(FIND_FILMS_BY_DIRECTOR_NAME_QUERY, search);
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
        if (film.getGenres() != null) {
            for (GenreDto genreDto : film.getGenres()) {
                insertFilmGenre(film.getId(), genreDto.getId());
            }
        }

        return film;
    }

    private void deleteFilmGenre(long filmId) {
        delete(DELETE_FILM_GENRE_QUERY, filmId);
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

    @Override
    public List<Film> getFilmRecommendations(long userId) {
        return findManyWithExtractor(FIND_RECOMMENDED_FILMS, userId, userId, userId);
    }

    @Override
    public List<Film> getCommonFilmsIdsWithAnotherUser(long userId, long friendId) {
        return findManyWithExtractor(COMMON_FILMS_ORDERED_POPULARITY, userId, friendId);
    }
}
