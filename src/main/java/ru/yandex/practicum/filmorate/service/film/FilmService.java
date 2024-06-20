package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private static final LocalDate EARLIEST_DATE = LocalDate.of(1895, Month.DECEMBER, 28);

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addFilm(Film film) {
        validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film findFilmById(long filmId) {
        return filmStorage.findFilmById(filmId).orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film updateFilm(Film newFilm) {
        validateFilm(newFilm);

        Film oldFilm = filmStorage.findFilmById(newFilm.getId())
                .orElseThrow(() -> new NotFoundException("Не найден фильм с id: " + newFilm.getId()));

        if (newFilm.getDescription() != null) {
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getName() != null) {
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDuration() != null) {
            oldFilm.setDuration(newFilm.getDuration());
        }
        if (newFilm.getReleaseDate() != null) {
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        return filmStorage.updateFilm(oldFilm);
    }

    public void deleteFilmById(long filmId) {
        boolean deleted = filmStorage.deleteFilmById(filmId);
        if (!deleted) {
            throw new NotFoundException("Фильм для удаления не найдено");
        }
    }

    public void likeFilm(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("фильм не найден"));
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь не найден"));

        film.getLikedUsersIDs().add(user.getId());
    }

    public void unlikeFilm(long filmId, long userId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("фильм не найден"));
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь не найден"));

        film.getLikedUsersIDs().remove(user.getId());
    }

    public List<Film> findPopularFilmsByCount(int count) {
        return filmStorage.findAllFilms().stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getLikedUsersIDs().size()).reversed())
                .limit(count)
                .toList();
    }

    private void validateFilm(Film film) {
        log.warn("Валидация фильма {}", film.getName());

        log.warn("Валидация имени {}", film.getName());
        if (film.getName() != null && film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }

        log.warn("Валидация описания {}", film.getDescription());
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            throw new ValidationException("Описание фильма не может быть длиннее 200 символов");
        }

        log.warn("Валидация даты релиза {}", film.getReleaseDate());
        if (film.getReleaseDate() != null && film.getReleaseDate().isBefore(EARLIEST_DATE)) {
            throw new ValidationException("Дата релиза фильма слишком старая");
        }

        log.warn("Валидация продолжительности {}", film.getDuration());
        if (film.getDuration() != null && film.getDuration().isNegative()) {
            throw new ValidationException("Продолжительность фильма не может быть ниже нуля");
        }

        log.info("Успешная валидация фильма {}", film.getName());
    }
}
