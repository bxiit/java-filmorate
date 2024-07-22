package ru.yandex.practicum.filmorate.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.model.event.Event;

import java.time.Instant;

@Mapper(componentModel = "spring")
public interface EventMapper {
    @Named("mapTimestamp")
    default Long mapTimestamp(Instant timestamp) {
        return timestamp.toEpochMilli();
    }

    @Mapping(target = "timestamp", qualifiedByName = "mapTimestamp")
    EventDto mapToEventDto(Event event);

    EventMapper MAPPER = Mappers.getMapper(EventMapper.class);
}
