package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.util.DurationDeserializer;
import ru.yandex.practicum.filmorate.util.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Film {

    private Long id;

    private String name;

    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseDate;

    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration duration;

    private Set<Long> likedUsersIDs;

    @JsonCreator
    private static Film filmJson(
            @JsonProperty Long id,
            @JsonProperty String name,
            @JsonProperty String description,
            @JsonProperty LocalDate releaseDate,
            @JsonProperty Duration duration
    ) {
        System.out.println("CREATING INSTANCE OF FILMMMM");
        return new Film(id, name, description, releaseDate, duration, new HashSet<>());
    }
}
