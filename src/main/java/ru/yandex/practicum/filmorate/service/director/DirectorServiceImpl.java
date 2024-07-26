package ru.yandex.practicum.filmorate.service.director;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    @Override
    public DirectorDto addDirector(DirectorDto directorDto) {
        Director director = DirectorMapper.MAPPER.mapToModel(directorDto);
        directorStorage.addDirector(director);
        return DirectorMapper.MAPPER.mapToDto(director);
    }

    @Override
    public DirectorDto addDirectorForFilm(Long filmId, Director director) {
        directorStorage.addDirectorForFilm(filmId, director);
        return DirectorMapper.MAPPER.mapToDto(director);
    }

    @Override
    public List<DirectorDto> findAllDirectors() {
        return directorStorage.findAllDirectors().stream()
                .map(DirectorMapper.MAPPER::mapToDto)
                .toList();
    }

    @Override
    public DirectorDto findDirectorById(Long directorId) {
        return directorStorage.findDirectorById(directorId)
                .map(DirectorMapper.MAPPER::mapToDto)
                .orElseThrow(() -> new NotFoundException("Режиссер не найден"));
    }

    @Override
    public List<Long> findFilmIdsByDirectorId(Long directorId) {
        return directorStorage.findFilmIdsByDirectorId(directorId);
    }

    @Override
    public DirectorDto updateDirector(DirectorDto directorDto) {
        Director director = DirectorMapper.MAPPER.mapToModel(findDirectorById(directorDto.getId()));
        director = DirectorMapper.MAPPER.updateDirectorFields(director, directorDto);
        directorStorage.updateDirector(director);
        return DirectorMapper.MAPPER.mapToDto(director);
    }

    @Override
    public void deleteDirector(Long directorId) {
        directorStorage.deleteDirector(directorId);
    }

    @Override
    public void deleteDirectorOfFilm(Long directorId) {
        directorStorage.deleteDirectorOfFilm(directorId);
    }

    @Override
    public void load(List<Film> films) {
        directorStorage.load(films);
    }
}
