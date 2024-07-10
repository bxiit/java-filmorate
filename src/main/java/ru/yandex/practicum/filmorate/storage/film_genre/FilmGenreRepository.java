package ru.yandex.practicum.filmorate.storage.film_genre;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmGenre;

import java.util.List;

public interface FilmGenreRepository /*extends JpaRepository<FilmGenre, Long>*/ {
    List<FilmGenre> findAllByFilm(Film film);
}
