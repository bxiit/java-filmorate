package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

@Mapper
public interface DirectorMapper {
    DirectorMapper MAPPER = Mappers.getMapper(DirectorMapper.class);
    Director mapToModel(DirectorDto directorDto);
    DirectorDto mapToDto(Director director);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "name", source = "request.name",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Director updateDirectorFields(@MappingTarget Director director, DirectorDto request);
}
