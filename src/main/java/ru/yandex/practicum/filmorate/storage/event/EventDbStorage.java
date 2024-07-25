package ru.yandex.practicum.filmorate.storage.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.event.Event;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Repository
public class EventDbStorage extends BaseRepository<Event> implements EventStorage {
    private static final String INSERT_EVENT_QUERY = """
            INSERT INTO EVENTS (TIMESTAMP, USER_ID, EVENT_TYPE_ID, OPERATION_ID, ENTITY_ID)
            VALUES ( ?, ?, (
                    select EVENT_TYPE_ID
                    from EVENT_TYPES
                    where EVENT_TYPE = ?),
                (
                    select OPERATION_ID
                    from OPERATIONS
                    where OPERATION = ?),
                ?)
            """;
    private static final String FIND_BY_USER_ID_QUERY = """
            select *
            from EVENTS e
            join EVENT_TYPES et on e.EVENT_TYPE_ID = et.EVENT_TYPE_ID
            join OPERATIONS o on e.OPERATION_ID = o.OPERATION_ID
            where e.USER_ID = ?
            order by e.TIMESTAMP asc
            """;

    public EventDbStorage(JdbcTemplate jdbc, RowMapper<Event> rowMapper, ResultSetExtractor<List<Event>> extractor) {
        super(jdbc, rowMapper, extractor);
    }

    @Override
    public Event createEvent(Event event) {
        long id = insert(
                INSERT_EVENT_QUERY,
                Timestamp.from(event.getTimestamp()),
                event.getUserId(),
                event.getEventType(),
                event.getOperation(),
                event.getEntityId()
        );
        event.setEventId(id);
        log.info("Добавлено событие: {}", event);
        return event;
    }

    @Override
    public List<Event> findFeedByUserId(long userId) {
        return findMany(FIND_BY_USER_ID_QUERY, userId);
    }
}