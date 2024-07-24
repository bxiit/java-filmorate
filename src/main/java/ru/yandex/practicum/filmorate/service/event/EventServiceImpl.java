package ru.yandex.practicum.filmorate.service.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.EventMapper;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
public class EventServiceImpl implements EventService {
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    @Autowired
    public EventServiceImpl(@Qualifier("EventDbStorage") EventStorage eventStorage,
                            @Qualifier("userDBStorage") UserStorage userStorage
    ) {
        this.eventStorage = eventStorage;
        this.userStorage = userStorage;
    }

    @Override
    public Event createEvent(long userId, long entityId, EventType eventType, Operation operation) {
        Event event = Event.builder()
                .timestamp(Instant.now())
                .userId(userId)
                .eventType(eventType.name())
                .operation(operation.name())
                .entityId(entityId)
                .build();
        return eventStorage.createEvent(event);
    }

    @Override
    public List<EventDto> findFeedByUserId(long userId) {
        if (userStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return eventStorage.findFeedByUserId(userId).stream()
                .map(EventMapper.MAPPER::mapToEventDto)
                .toList();
    }
}
