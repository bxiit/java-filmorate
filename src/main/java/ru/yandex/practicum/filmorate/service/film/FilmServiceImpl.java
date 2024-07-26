package ru.yandex.practicum.filmorate.service.film;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.DirectorMapper;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.director.DirectorService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.util.enums.search.SearchBy;
import ru.yandex.practicum.filmorate.util.enums.sort.SortBy;

import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static ru.yandex.practicum.filmorate.util.constants.Constants.addWildCards;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;

    private final MpaStorage mpaStorage;

    private final GenreStorage genreStorage;

    private final DirectorService directorService;
    private final JdbcTemplate jdbc;

    private Map<SearchBy, Function<String, List<Film>>> searchByMap;

    @PostConstruct
    void postConstruct() {
        searchByMap = Map.of(
                SearchBy.TITLE, filmStorage::findFilmsByQueryFilmTitle,
                SearchBy.DIRECTOR, filmStorage::findFilmsByQueryDirectorName
        );
    }

    @Override
    public FilmDto addFilm(FilmDto request) {
        Film film = FilmMapper.MAPPER.mapNewFilmToFilm(request);
        film = filmStorage.addFilm(film);
        addDirectorForFilm(film);
        loadFilmParams(film);
        return FilmMapper.MAPPER.mapToFilmDto(film);
    }

    @Override
    public FilmDto findFilmById(long filmId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        loadFilmParams(film);
        return FilmMapper.MAPPER.mapToFilmDto(film);
    }

    @Override
    public List<FilmDto> findFilmsByDirector(Long directorId, String sort) {
        directorService.findDirectorById(directorId); //Проверка есть ли режиссер с таким id
        SortBy sortBy = SortBy.valueOf(sort.toUpperCase());
        List<Film> films = directorService.findFilmIdsByDirectorId(directorId).stream()
                .map(this::findFilmById)
                .map(FilmMapper.MAPPER::mapNewFilmToFilm)
                .toList();
        loadFilmParams(films);
        return films.stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .sorted(sortBy.sortComparator())
                .toList();
    }

    @Override
    public List<FilmDto> findAllFilms() {
        List<Film> films = filmStorage.findAllFilms().stream()
                .toList();
        loadFilmParams(films);
        return films.stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .toList();
    }

    @Override
    public FilmDto updateFilm(FilmDto request) {
        Film film = filmStorage.findFilmById(request.getId())
                .map(f -> FilmMapper.MAPPER.updateFilmFields(f, request))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        filmStorage.updateFilm(film);
        updateDirectorOfFilm(request);
        loadFilmParams(film);

        return FilmMapper.MAPPER.mapToFilmDto(film);
    }

    @Override
    public void deleteFilmById(long filmId) {
        boolean deleted = filmStorage.deleteFilmById(filmId);
        if (!deleted) {
            throw new NotFoundException("Фильм не найден");
        }
    }

    @Override
    public List<FilmDto> findPopularFilmsByCount(int count) {
        List<Film> films = filmStorage.findPopularFilms(count).stream()
                .toList();
        loadFilmParams(films);
        return films.stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .toList();
    }

    @Override
    public List<FilmDto> findPopularFilmsByGenreAndYear(int count, Long genreId, Integer year) {
        List<Film> films = filmStorage.findPopularFilmsByGenreAndYear(count, genreId, year).stream()
                .toList();
        loadFilmParams(films);
        return films.stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .toList();
    }


    @Override
    public List<FilmDto> findFilmsByQuery(String search, SearchBy[] by) {
        final String searchArgument = addWildCards(search);

        List<Film> films = Arrays.stream(by)
                .flatMap(searchBy -> searchByMap.get(searchBy).apply(searchArgument).stream())
                .toList();
        loadFilmParams(films);
        return films.stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .sorted(SortBy.LIKES.sortComparator())
                .toList();
    }

    @Override
    public void likeFilm(long filmId, long userId) {
        if (filmId < 0 || userId < 0) {
            throw new NotFoundException();
        }
        FilmDto filmDto = findFilmById(filmId);
        if (!filmDto.getLikedUsersIDs().contains(userId)) {
            filmStorage.likeFilm(filmId, userId);
        }
    }

    @Override
    public void unlikeFilm(long filmId, long userId) {
        if (filmId < 0 || userId < 0) {
            throw new NotFoundException();
        }
        filmStorage.unlikeFilm(filmId, userId);
    }

    @Override
    public List<FilmDto> getFilmRecommendations(long userId) {
        List<Film> films = filmStorage.getFilmRecommendations(userId).stream()
                .toList();
        loadFilmParams(films);
        return films.stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .toList();
    }

    @Override
    public List<FilmDto> commonFilmsWithFriend(long userId, long friendId) {
        List<Film> films = filmStorage.getCommonFilmsIdsWithAnotherUser(userId, friendId).stream()
                .toList();
        loadFilmParams(films);
        return films.stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .toList();
    }

    private void loadFilmParams(Film film) {
        genreStorage.load(List.of(film));
        mpaStorage.load(List.of(film));
        directorService.load(List.of(film));
    }

    private void loadFilmParams(List<Film> films) {
        genreStorage.load(films);
        mpaStorage.load(films);
        directorService.load(films);
    }

    private void updateDirectorOfFilm(FilmDto filmDto) {
        directorService.deleteDirectorOfFilm(filmDto.getId());
        if (filmDto.getDirectors() == null) {
            return;
        }
        jdbc.batchUpdate("""
                        INSERT INTO film_director (film_id, director_id)
                        VALUES (?, ?);
                        """, filmDto.getDirectors(), filmDto.getDirectors().size(),
                (PreparedStatement ps, DirectorDto directorDto) -> {
                    ps.setLong(1, filmDto.getId());
                    ps.setLong(2, directorDto.getId());
                }
        );
    }

    private void addDirectorForFilm(Film film) {
        if (film.getDirectors() == null) {
            return;
        }
        film.getDirectors().stream()
                .map(DirectorDto::getId)
                .forEach(directorId -> {
                    DirectorDto directorDto = directorService.findDirectorById(directorId);
                    Director director = DirectorMapper.MAPPER.mapToModel(directorDto);
                    directorService.addDirectorForFilm(film.getId(), director);
                });
    }
}
