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
import ru.yandex.practicum.filmorate.storage.genre.GenreDBStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDBStorage;
import ru.yandex.practicum.filmorate.util.sort.SortBy;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    @Qualifier("FilmDBStorage")
    private final FilmStorage filmStorage;

    private final MpaDBStorage mpaStorage;

    private final GenreDBStorage genreStorage;

    private final DirectorService directorService;

    public FilmDto addFilm(FilmDto request) {
        setGenreName(request);
        setMpaName(request);
        setDirectorName(request);
        Film film = FilmMapper.MAPPER.mapNewFilmToFilm(request);
        film = filmStorage.addFilm(film);
        addDirectorForFilm(film);
        return FilmMapper.MAPPER.mapToFilmDto(film);
    }

    public FilmDto findFilmById(long filmId) {
        FilmDto filmDto = filmStorage.findFilmById(filmId)
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        setGenreName(filmDto);
        setMpaName(filmDto);
        setDirectorName(filmDto);
        return filmDto;
    }

    public List<FilmDto> findFilmsByDirector(Long directorId, String sort) {
        SortBy sortBy = SortBy.valueOf(sort.toUpperCase());
        return directorService.findFilmIdsByDirectorId(directorId).stream()
                .map(this::findFilmById)
                .sorted(sortBy.sortComparator())
                .toList();
    }

    public List<FilmDto> findAllFilms() {
        return filmStorage.findAllFilms().stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .map(this::setGenreName)
                .map(this::setMpaName)
                .map(this::setDirectorName)
                .toList();
    }

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

    public void deleteFilmById(long filmId) {
        boolean deleted = filmStorage.deleteFilmById(filmId);
        if (!deleted) {
            throw new NotFoundException("Фильм не найден");
        }
    }

    public List<FilmDto> findPopularFilmsByCount(int count) {
        return filmStorage.findPopularFilms(count).stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .map(this::setGenreName)
                .map(this::setMpaName)
                .map(this::setDirectorName)
                .toList();
    }

    public List<FilmDto> findPopularFilmsByGenreAndYear(int count, Long genreId, Integer year) {
        return filmStorage.findPopularFilmsByGenreAndYear(count, genreId, year).stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .map(this::setGenreName)
                .map(this::setMpaName)
                .toList();
    }

    public void likeFilm(long filmId, long userId) {
        filmStorage.likeFilm(filmId, userId);
    }

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

    public List<FilmDto> getFilmRecommendations(long userId) {
        return filmStorage.getFilmRecommendations(userId).stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .map(this::setGenreName)
                .map(this::setMpaName)
                .toList();
    }

    public List<FilmDto> commonFilmsWithFriend(long userId, long friendId) {
        return filmStorage.getCommonFilmsIdsWithAnotherUser(userId, friendId).stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .map(this::setGenreName)
                .map(this::setMpaName)
                .toList();
    }
}
