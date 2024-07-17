package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        Long maxId = getMaxId();
        film.setId(maxId);
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public Optional<Film> findFilmById(long filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public List<Film> findAllFilms() {
        return films.values().stream()
                .toList();
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return films.get(film.getId());
    }

    @Override
    public boolean deleteFilmById(long filmId) {
        Film removedFilm = films.remove(filmId);
        return filmId == removedFilm.getId();
    }

    @Override
    public List<Film> findPopularFilms(long count) {
        return List.of();
    }

    @Override
    public List<Film> findPopularFilmsByGenreAndYear(int count, Long genreId, Integer year) {
        List<Film> films = this.films.values().stream().toList();

        if (year != null) {
            films = films.stream()
                    .filter(film -> film.getReleaseDate().getYear() == year)
                    .toList();
        }
        if (genreId != null) {
            films = films.stream()
                    .filter(film -> film.getGenres().stream()
                            .anyMatch(genreDto -> genreDto.getId().equals(genreId))
                    )
                    .toList();
        }

        return films.stream()
                .sorted(Comparator.comparingInt(film -> film.getLikedUsersIDs().size()))
                .toList().reversed();
    }

    @Override
    public boolean likeFilm(long filmId, long userId) {
        return false;
    }

    @Override
    public boolean unlikeFilm(long filmId, long userId) {
        return false;
    }

    private Long getMaxId() {
        return IdGenerator.getMaxIdOfFilms();
    }
}
