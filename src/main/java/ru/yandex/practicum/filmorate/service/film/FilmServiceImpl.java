package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.util.constants.Constants.COMMA;
import static ru.yandex.practicum.filmorate.util.constants.Constants.addWildCards;
import static ru.yandex.practicum.filmorate.util.parser.StringEnumParser.parseStringToEnum;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    @Qualifier("FilmDBStorage")
    private final FilmStorage filmStorage;

    private final MpaStorage mpaStorage;

    private final GenreStorage genreStorage;

    private final DirectorService directorService;

    @Override
    public FilmDto addFilm(FilmDto request) {
        setGenreName(request);
        setMpaName(request);
        setDirectorName(request);
        Film film = FilmMapper.MAPPER.mapNewFilmToFilm(request);
        film = filmStorage.addFilm(film);
        addDirectorForFilm(film);
        return FilmMapper.MAPPER.mapToFilmDto(film);
    }

    @Override
    public FilmDto findFilmById(long filmId) {
        FilmDto filmDto = filmStorage.findFilmById(filmId)
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        setGenreName(filmDto);
        setMpaName(filmDto);
        setDirectorName(filmDto);
        return filmDto;
    }

    @Override
    public List<FilmDto> findFilmsByDirector(Long directorId, String sort) {
        SortBy sortBy = SortBy.valueOf(sort.toUpperCase());
        return directorService.findFilmIdsByDirectorId(directorId).stream()
                .map(this::findFilmById)
                .sorted(sortBy.sortComparator())
                .toList();
    }

    @Override
    public List<FilmDto> findAllFilms() {
        return filmStorage.findAllFilms().stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .map(this::setGenreName)
                .map(this::setMpaName)
                .map(this::setDirectorName)
                .toList();
    }

    @Override
    public FilmDto updateFilm(FilmDto request) {
        Film film = filmStorage.findFilmById(request.getId())
                .map(f -> FilmMapper.MAPPER.updateFilmFields(f, request))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        filmStorage.updateFilm(film);
        updateDirectorOfFilm(request);
        FilmDto updatedFilmDto = FilmMapper.MAPPER.mapToFilmDto(film);
        setGenreName(updatedFilmDto);
        setMpaName(updatedFilmDto);
        setDirectorName(updatedFilmDto);

        return updatedFilmDto;
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
        return filmStorage.findPopularFilms(count).stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .map(this::setGenreName)
                .map(this::setMpaName)
                .map(this::setDirectorName)
                .toList();
    }

    @Override
    public List<FilmDto> findPopularFilmsByGenreAndYear(int count, Long genreId, Integer year) {
        return filmStorage.findPopularFilmsByGenreAndYear(count, genreId, year).stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .map(this::setGenreName)
                .map(this::setMpaName)
                .toList();
    }

    @Override
    public List<FilmDto> findFilmsByQuery(String search, String by) {
        List<Film> result = new ArrayList<>();
        search = addWildCards(search);
        Set<SearchBy> searchBys = parseStringToEnum(by, COMMA, SearchBy.class);
        for (SearchBy s : searchBys) {
            if (Objects.equals(s, SearchBy.TITLE))
                result.addAll(filmStorage.findFilmsByQueryFilmTitle(search));
            else if (Objects.equals(s, SearchBy.DIRECTOR)) {
                result.addAll(filmStorage.findFilmsByQueryDirectorName(search));
            }
        }
        return result.stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .sorted(SortBy.LIKES.sortComparator())
                .map(this::setGenreName)
                .map(this::setMpaName)
                .map(this::setDirectorName)
                .toList()
                .reversed();
    }


    @Override
    public List<FilmDto> findFilmsByQueryOpt(String search, String by) {
        final String searchArgument = addWildCards(search);
        Map<SearchBy, Function<String, List<Film>>> searchByMap = Map.of(
                SearchBy.TITLE, filmStorage::findFilmsByQueryFilmTitle,
                SearchBy.DIRECTOR, filmStorage::findFilmsByQueryDirectorName
        );
        return parseStringToEnum(by, COMMA, SearchBy.class).stream()
                .flatMap(searchBy -> searchByMap.get(searchBy).apply(searchArgument).stream())
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .sorted(SortBy.LIKES.sortComparator())
                .map(this::setGenreName)
                .map(this::setMpaName)
                .map(this::setDirectorName)
                .toList();
    }

    @Override
    public void likeFilm(long filmId, long userId) {
        filmStorage.likeFilm(filmId, userId);
    }

    @Override
    public void unlikeFilm(long filmId, long userId) {
        filmStorage.unlikeFilm(filmId, userId);
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

    private FilmDto setMpaName(FilmDto filmDto) {
        if (filmDto.getMpa() == null || filmDto.getMpa().getId() == null) {
            return filmDto;
        }
        mpaStorage.findMpaById(filmDto.getMpa().getId())
                .ifPresentOrElse(
                        mpa -> filmDto.getMpa().setName(mpa.getName()),
                        () -> {
                            throw new ValidationException("Не валидный рейтинг");
                        }
                );
        return filmDto;
    }

    private FilmDto setGenreName(FilmDto filmDto) {
        if (filmDto.getGenres() == null) {
            return filmDto;
        }
        filmDto.getGenres()
                .forEach(genreDto -> genreStorage.findGenreById(genreDto.getId())
                        .ifPresentOrElse(
                                genre -> genreDto.setName(genre.getName()),
                                () -> {
                                    throw new ValidationException("Не валидный жанр");
                                }
                        ));
        return filmDto;
    }

    private FilmDto setDirectorName(FilmDto filmDto) {
        if (filmDto.getDirectors() == null) {
            return filmDto;
        }
        Set<DirectorDto> directorsWithName = filmDto.getDirectors().stream()
                .map(DirectorDto::getId)
                .map(directorService::findDirectorById)
                .collect(Collectors.toSet());
        filmDto.setDirectors(directorsWithName);
        return filmDto;
    }

    private void updateDirectorOfFilm(FilmDto filmDto) {
        if (filmDto.getDirectors() == null) {
            return;
        }
        directorService.deleteDirectorOfFilm(filmDto.getId());
        for (DirectorDto directorDto : filmDto.getDirectors()) {
            Director director = DirectorMapper.MAPPER.mapToModel(directorDto);
            directorService.addDirectorForFilm(filmDto.getId(), director);
        }
    }

    @Override
    public List<FilmDto> getFilmRecommendations(long userId) {
        return filmStorage.getFilmRecommendations(userId).stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .map(this::setGenreName)
                .map(this::setMpaName)
                .toList();
    }

    @Override
    public List<FilmDto> commonFilmsWithFriend(long userId, long friendId) {
        return filmStorage.getCommonFilmsIdsWithAnotherUser(userId, friendId).stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .map(this::setGenreName)
                .map(this::setMpaName)
                .toList();
    }
}
