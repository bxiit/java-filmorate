package ru.yandex.practicum.filmorate.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class EventService {
    private final EventStorage eventStorage;

    @Autowired
    public EventService(@Qualifier("EventDbStorage") EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    public Event addEvent(long userId, long entityId, EventType eventType, Operation operation) {
        Event event = Event.builder()
                .timestamp(Instant.now())
                .userId(userId)
                .eventType(eventType.name())
                .operation(operation.name())
                .entityId(entityId)
                .build();
        return eventStorage.addEvent(event);
    }

    public List<Event> findFeedByUserId(long userId) {
        return eventStorage.findFeedByUserId(userId);
    }
}
