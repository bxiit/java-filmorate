package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.util.DurationDeserializer;
import ru.yandex.practicum.filmorate.util.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Film.
 */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@FieldNameConstants
public class Film {

    public Film() {
        this.likedUsersIDs = new HashSet<>();
        this.genres = new TreeSet<>();
    }

    private Long id;

    private String name;

    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseDate;

    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration duration;

    private Set<Long> likedUsersIDs;

    private MpaDto mpa;

    private Set<GenreDto> genres;

    private Set<DirectorDto> directors;
}
