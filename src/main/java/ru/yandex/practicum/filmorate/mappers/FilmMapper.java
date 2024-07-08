package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Set;
import java.util.TreeSet;

@Mapper(componentModel = "spring")
public interface FilmMapper {

    @Named("mapToTreeSet")
    default TreeSet<GenreDto> mapToTreeSet(Set<GenreDto> genres) {
        return genres != null ? new TreeSet<>(genres) : new TreeSet<>();
    }

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "releaseDate", source = "request.releaseDate")
    @Mapping(target = "duration", source = "request.duration")
    @Mapping(target = "mpa", source = "request.mpa")
    @Mapping(target = "genres", qualifiedByName = "mapToTreeSet")
    Film mapNewFilmToFilm(NewFilmRequest request);

    @Mapping(target = "genres", qualifiedByName = "mapToTreeSet")
    FilmDto mapToFilmDto(Film film);

    @Mapping(target = "genres", qualifiedByName = "mapToTreeSet")
    Film mapToFilmModel(FilmDto filmDto);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "request.name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", source = "request.description", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "releaseDate", source = "request.releaseDate", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "duration", source = "request.duration", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "mpa", source = "request.mpa", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Film updateFilmFields(@MappingTarget Film film, UpdateFilmRequest request);

    FilmMapper MAPPER = Mappers.getMapper(FilmMapper.class);
}
