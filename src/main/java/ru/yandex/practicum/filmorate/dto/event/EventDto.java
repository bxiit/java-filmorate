package ru.yandex.practicum.filmorate.dto.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class EventDto {
    private Long eventId;
    private Long timestamp;
    private Long userId;
    private String eventType;
    private String operation;
    private Long entityId;
}
