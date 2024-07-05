package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDBStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDBStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate EARLIEST_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    @Qualifier("FilmDBStorage")
    private final FilmStorage filmStorage;
    @Qualifier("UserDBStorage")
    private final UserStorage userStorage;

    private final MpaDBStorage mpaStorage;

    private final GenreDBStorage genreStorage;

    public FilmDto addFilm(NewFilmRequest request) {
        checkMpaExistence(request.getMpa());
        checkGenreExistence(request.getGenres());
        Film film = FilmMapper.mapToFilm(request);
        film = filmStorage.addFilm(film);
        FilmDto filmDto = FilmMapper.mapToFilmDto(film);
        setGenreName(filmDto);
        setMpaName(filmDto);
        return filmDto;
    }

    public FilmDto findFilmById(long filmId) {
        FilmDto filmDto = filmStorage.findFilmById(filmId)
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        setGenreName(filmDto);
        setMpaName(filmDto);
        return filmDto;
    }

    public List<FilmDto> findAllFilms() {
        return filmStorage.findAllFilms().stream()
                .map(FilmMapper::mapToFilmDto)
                .map(this::setGenreName)
                .map(this::setMpaName)
                .toList();
    }

    public FilmDto updateFilm(UpdateFilmRequest request) {
        Film film = filmStorage.findFilmById(request.getId())
                .map(f -> FilmMapper.updateFilmFields(f, request))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
        filmStorage.updateFilm(film);
        FilmDto updatedFilmDto = FilmMapper.mapToFilmDto(film);
        setGenreName(updatedFilmDto);
        setMpaName(updatedFilmDto);

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
                .map(FilmMapper::mapToFilmDto)
                .map(this::setGenreName)
                .map(this::setMpaName)
                .toList();
    }

    public void likeFilm(long filmId, long userId) {
        likeFilmAction(filmId, userId, true);
    }

    public void unlikeFilm(long filmId, long userId) {
        likeFilmAction(filmId, userId, false);
    }

    private void likeFilmAction(long filmId, long userId, boolean isLike) {
        try {
            if (isLike) {
                filmStorage.likeFilm(filmId, userId);
            } else {
                filmStorage.unlikeFilm(filmId, userId);
            }
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
        }
    }

    private FilmDto setMpaName(FilmDto filmDto) {
        if (filmDto.getMpa() != null && filmDto.getMpa().getId() != null) {
            Mpa mpa = mpaStorage.findMpaById(filmDto.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("Рейтинг не найден"));
            filmDto.getMpa().setName(mpa.getName());
        }
        return filmDto;
    }

    private FilmDto setGenreName(FilmDto filmDto) {
        filmDto.getGenres()
                .forEach(genreDto -> {
                    genreStorage.findGenreById(genreDto.getId())
                            .ifPresent(genre -> genreDto.setName(genre.getName()));
                });
        return filmDto;
    }

//    private void validateUpdateFilm(UpdateFilmRequest request) {
//        log.warn("Валидация фильма {}", request.getName());
//
//        log.warn("Валидация имени {}", request.getName());
//        if (request.getName() != null && request.getName().isEmpty()) {
//            throw new ValidationException("Название фильма не может быть пустым");
//        }
//
//        log.warn("Валидация описания {}", request.getDescription());
//        if (request.getDescription() != null && request.getDescription().length() > 200) {
//            throw new ValidationException("Описание фильма не может быть длиннее 200 символов");
//        }
//
//        log.warn("Валидация даты релиза {}", request.getReleaseDate());
//        if (request.getReleaseDate() != null && request.getReleaseDate().isBefore(EARLIEST_DATE)) {
//            throw new ValidationException("Дата релиза фильма слишком старая");
//        }
//
//        log.warn("Валидация продолжительности {}", request.getDuration());
//        if (request.getDuration() != null && request.getDuration().isNegative()) {
//            throw new ValidationException("Продолжительность фильма не может быть ниже нуля");
//        }
//
//        log.info("Успешная валидация фильма {}", request.getName());
//    }
//
//    private void validateNewFilm(NewFilmRequest request) {
//        log.warn("Валидация фильма {}", request.getName());
//
//        log.warn("Валидация имени {}", request.getName());
//        if (request.getName() != null && request.getName().isEmpty()) {
//            throw new ValidationException("Название фильма не может быть пустым");
//        }
//
//        log.warn("Валидация описания {}", request.getDescription());
//        if (request.getDescription() != null && request.getDescription().length() > 200) {
//            throw new ValidationException("Описание фильма не может быть длиннее 200 символов");
//        }
//
//        log.warn("Валидация даты релиза {}", request.getReleaseDate());
//        if (request.getReleaseDate() != null && request.getReleaseDate().isBefore(EARLIEST_DATE)) {
//            throw new ValidationException("Дата релиза фильма слишком старая");
//        }
//
//        log.warn("Валидация продолжительности {}", request.getDuration());
//        if (request.getDuration() != null && request.getDuration().isNegative()) {
//            throw new ValidationException("Продолжительность фильма не может быть ниже нуля");
//        }
//
//        log.info("Успешная валидация фильма {}", request.getName());
//    }

    private void checkGenreExistence(Set<GenreDto> genres) {
        if (genres == null) {
            return;
        }
        for (GenreDto genreDto : genres) {
            if (!doesGenreExist(genreDto.getId())) {
                throw new ValidationException("Не существующий жанр");
            }
        }
    }

    private void checkMpaExistence(MpaDto mpaDto) {
        if (mpaDto != null && !doesMpaExist(mpaDto.getId())) {
            throw new ValidationException("Не существующий рейтинг");
        }
    }

    private boolean doesMpaExist(long mpaId) {
        return mpaStorage.findMpaById(mpaId).isPresent();
    }

    private boolean doesGenreExist(long genreId) {
        return genreStorage.findGenreById(genreId).isPresent();
    }
}
