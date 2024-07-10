package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmUserLikes;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface FilmMapper {

    @Named("mapToTreeSet")
    default TreeSet<GenreDto> mapToTreeSet(Set<GenreDto> genres) {
        return genres != null ? new TreeSet<>(genres) : new TreeSet<>();
    }

    @Named("mapMpaToMpaDto")
    default MpaDto mapMpaToMpaDto(Mpa mpa) {
        return MpaMapper.MPA_MAPPER.mapToDto(mpa);
    }

    @Named("mapMpaDtoToMpa")
    default Mpa mapMpaDtoToMpa(MpaDto mpaDto) {
        return MpaMapper.MPA_MAPPER.mapToModel(mpaDto);
    }

    @Named("mapFilmUserLikesToSetLong")
    default Set<Long> mapFilmUserLikesToSetLong(Set<FilmUserLikes> likes) {
        if (likes == null) {
            return null;
        }
        return likes.stream()
                .map(FilmUserLikes::getUser)
                .map(User::getId)
                .collect(Collectors.toSet());
    }

    @Mapping(target = "name", source = "request.name")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "releaseDate", source = "request.releaseDate")
    @Mapping(target = "duration", source = "request.duration")
    @Mapping(target = "mpa", source = "request.mpa", qualifiedByName = "mapMpaDtoToMpa")
    @Mapping(ignore = true,
            target = "likes", source = "request.likedUserIds")
    Film mapNewFilmToFilm(FilmDto request);

    @Mapping(target = "mpa", source = "film.mpa", qualifiedByName = "mapMpaToMpaDto")
    @Mapping(target = "likedUserIds", source = "film.likes", qualifiedByName = "mapFilmUserLikesToSetLong")
    FilmDto mapToFilmDto(Film film);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "request.name", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "description", source = "request.description", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "releaseDate", source = "request.releaseDate", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "duration", source = "request.duration", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "mpa", source = "request.mpa", qualifiedByName = "mapMpaDtoToMpa",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Film updateFilmFields(@MappingTarget Film film, FilmDto request);

    FilmMapper MAPPER = Mappers.getMapper(FilmMapper.class);
}
