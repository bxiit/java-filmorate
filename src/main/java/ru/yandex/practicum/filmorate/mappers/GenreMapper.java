package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    GenreMapper GENRE_MAPPER = Mappers.getMapper(GenreMapper.class);

    GenreDto mapToDto(Genre genre);

    Genre mapToModel(GenreDto genreDto);
}
