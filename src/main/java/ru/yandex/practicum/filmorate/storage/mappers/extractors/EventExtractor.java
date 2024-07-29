package ru.yandex.practicum.filmorate.storage.mappers.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.event.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class EventExtractor implements ResultSetExtractor<List<Event>> {
    @Override
    public List<Event> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, Event> events = new HashMap<>();

        while (rs.next()) {
            Event event = Event.builder()
                    .eventId(rs.getLong("event_id"))
                    .timestamp(rs.getTimestamp("timestamp").toInstant())
                    .userId(rs.getLong("user_id"))
                    .eventType(rs.getString("event_type"))
                    .operation(rs.getString("operation"))
                    .entityId(rs.getLong("user_id"))
                    .build();
            events.put(event.getEventId(), event);
        }
        return new ArrayList<>(events.values());
    }
}
