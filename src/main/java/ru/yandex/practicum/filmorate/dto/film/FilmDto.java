package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.util.annotations.MinDuration;
import ru.yandex.practicum.filmorate.util.annotations.NotBefore;
import ru.yandex.practicum.filmorate.util.parser.DurationDeserializer;
import ru.yandex.practicum.filmorate.util.parser.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class FilmDto {

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

    private Set<DirectorDto> directors;
}
