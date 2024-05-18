package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.convert.DurationFormat;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Film.
 */
@Data
@Builder(toBuilder = true)
public class Film {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE;
    private static final Logger log = LoggerFactory.getLogger(Film.class);
    public static final int SECONDS_IN_MINUTE = 60;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    @NotEmpty(message = "Название фильма не может быть пустым")
    private String name;

    @JsonProperty("description")
    @Size(max = 200, message = "Описание фильма не должно превышать 200 символов")
    private String description;

    @JsonProperty("releaseDate")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseDate;

    @JsonProperty("duration")
    @DurationFormat(DurationStyle.ISO8601)
    private Duration duration;

    @JsonCreator
    static Film createFilmJson(
            @JsonProperty("id") Long id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("releaseDate") LocalDate releaseDate,
            @JsonProperty("duration") Duration duration
    ) {
        return Film.builder()
                .id(id)
                .name(name)
                .description(description)
                .releaseDate(releaseDate)
                .duration(duration)
                .build();
    }
}
