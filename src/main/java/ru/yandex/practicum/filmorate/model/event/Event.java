package ru.yandex.practicum.filmorate.model.event;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder(toBuilder = true)
public class Event {
    private Long eventId;
    private Instant timestamp;
    private Long userId;
    private String eventType;
    private String operation;
    private Long entityId;
}
