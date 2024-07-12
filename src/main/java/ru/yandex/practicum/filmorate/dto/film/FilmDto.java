package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.util.DurationDeserializer;
import ru.yandex.practicum.filmorate.util.DurationSerializer;
import ru.yandex.practicum.filmorate.util.annotations.MinDuration;
import ru.yandex.practicum.filmorate.util.annotations.NotBefore;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
public class FilmDto {

    public FilmDto() {
        this.likedUsersIDs = new HashSet<>();
        this.genres = new TreeSet<>();
    }

    @Positive(message = "Не валидный идентификатор")
    private Long id;

    @NotEmpty(message = "Название фильма не может быть пустым")
    private String name;

    @Size(max = 200, message = "Описание фильма не может быть длиннее 200 символов")
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotBefore("1895-12-28")
    private LocalDate releaseDate;

    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    @MinDuration(0)
    private Duration duration;

    private Set<Long> likedUsersIDs;

    private MpaDto mpa;

    private Set<GenreDto> genres;
}
