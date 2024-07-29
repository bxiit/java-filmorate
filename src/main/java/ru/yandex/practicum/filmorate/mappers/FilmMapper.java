package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

@Mapper()
public interface FilmMapper {

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "releaseDate", source = "request.releaseDate")
    @Mapping(target = "duration", source = "request.duration")
    @Mapping(target = "mpa", source = "request.mpa")
    @Mapping(target = "genres", source = "request.genres",
            defaultExpression = "java(new java.util.LinkedHashSet<>())"
    )
    @Mapping(target = "directors", source = "request.directors",
            defaultExpression = "java(new java.util.HashSet<>())")
    Film mapNewFilmToFilm(FilmDto request);

    @Mapping(target = "genres", source = "film.genres",
            defaultExpression = "java(new java.util.LinkedHashSet<>())")
    @Mapping(target = "likedUsersIDs", source = "film.likedUsersIDs",
            defaultExpression = "java(new java.util.HashSet<>())")
    @Mapping(target = "directors", source = "film.directors",
            defaultExpression = "java(new java.util.HashSet<>())")
    FilmDto mapToFilmDto(Film film);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "request.name",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", source = "request.description",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "releaseDate", source = "request.releaseDate",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "duration", source = "request.duration",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "mpa", source = "request.mpa",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "directors", source = "request.directors")
    Film updateFilmFields(@MappingTarget Film film, FilmDto request);

    FilmMapper MAPPER = Mappers.getMapper(FilmMapper.class);
}
