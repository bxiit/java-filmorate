package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.convert.DurationFormat;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.util.DurationDeserializer;
import ru.yandex.practicum.filmorate.util.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("releaseDate")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseDate;

    @JsonProperty("duration")
    @DurationFormat(DurationStyle.ISO8601)
    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration duration;

//    @JsonCreator
//    static Film createFilmJson(
//            @JsonProperty("id") Long id,
//            @JsonProperty("name") String name,
//            @JsonProperty("description") String description,
//            @JsonProperty("releaseDate") LocalDate releaseDate,
//            @JsonProperty("duration") Duration duration
//    ) {
//        return Film.builder()
//                .id(id)
//                .name(name)
//                .description(description)
//                .releaseDate(releaseDate)
//                .duration(duration)
//                .build();
//    }
}
