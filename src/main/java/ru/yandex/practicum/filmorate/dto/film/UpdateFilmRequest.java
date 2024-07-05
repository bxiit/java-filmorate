package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.util.DurationDeserializer;
import ru.yandex.practicum.filmorate.util.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;
import java.util.TreeSet;

@Getter
public class UpdateFilmRequest {

    public UpdateFilmRequest() {
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

    private MpaDto mpa;

    private Set<GenreDto> genres;

    public boolean hasName() {
        return this.getName() != null;
    }

    public boolean hasDescription() {
        return this.getDescription() != null;
    }

    public boolean hasReleaseDate() {
        return this.getReleaseDate() != null;
    }

    public boolean hasDuration() {
        return this.getDuration() != null;
    }

    public boolean hasMpa() {
        return this.getMpa() != null;
    }

    public boolean hasGenres() {
        return this.getGenres() != null;
    }
}
