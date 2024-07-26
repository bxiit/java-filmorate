package ru.yandex.practicum.filmorate.service.director;

import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorService {
    DirectorDto addDirector(DirectorDto directorDto);

    DirectorDto addDirectorForFilm(Long filmId, Director director);

    List<DirectorDto> findAllDirectors();

    DirectorDto findDirectorById(Long directorId);

    List<Long> findFilmIdsByDirectorId(Long directorId);

    DirectorDto updateDirector(DirectorDto directorDto);

    void deleteDirector(Long directorId);

    void deleteDirectorOfFilm(Long directorId);

    void load(List<Film> films);
}
