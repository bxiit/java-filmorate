package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.model.Mpa;

@Mapper(componentModel = "spring")
public interface MpaMapper {
    MpaMapper MPA_MAPPER = Mappers.getMapper(MpaMapper.class);

    @Mapping(target = "id", source = "mpa.id")
    @Mapping(target = "name", source = "mpa.name")
    MpaDto mapToDto(Mpa mpa);

    @Mapping(target = "id", source = "mpaDto.id")
    @Mapping(target = "name", source = "mpaDto.name")
    Mpa mapToModel(MpaDto mpaDto);
}
