package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.util.DurationDeserializer;
import ru.yandex.practicum.filmorate.util.DurationSerializer;
import ru.yandex.practicum.filmorate.util.validator.annotations.MinDuration;
import ru.yandex.practicum.filmorate.util.validator.annotations.NotBefore;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

@Data
public class NewFilmRequest {

    @NotEmpty(message = "Название фильма не может быть пустым")
    private String name;

    @NotNull
    @Size(max = 200, message = "Описание фильма не может быть длиннее 200 символов")
    private String description;

    @NotBefore("1895-12-28")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate releaseDate;

    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    @MinDuration(0)
    private Duration duration;

    private MpaDto mpa;

    @JsonProperty
    private Set<GenreDto> genres;
}
