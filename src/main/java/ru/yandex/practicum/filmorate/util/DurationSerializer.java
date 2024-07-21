package ru.yandex.practicum.filmorate.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.Duration;

public class DurationSerializer extends JsonSerializer<Duration> {

    public static final long SECONDS_IN_MINUTE = 60L;

    @Override
    public void serialize(Duration value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            // Преобразование секунд в минуты
            Duration duration = value.multipliedBy(SECONDS_IN_MINUTE);
            gen.writeNumber(duration.toMinutes());
        }
    }
}
