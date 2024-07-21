package ru.yandex.practicum.filmorate.service.event;

import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;

import java.util.List;

public interface EventService {
    Event createEvent(long userId, long entityId, EventType eventType, Operation operation);

    List<EventDto> findFeedByUserId(long userId);
}
