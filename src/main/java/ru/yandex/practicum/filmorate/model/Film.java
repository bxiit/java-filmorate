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
import ru.yandex.practicum.filmorate.util.parser.DurationDeserializer;
import ru.yandex.practicum.filmorate.util.parser.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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
        this.genres = new LinkedHashSet<>();
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

    private LinkedHashSet<GenreDto> genres;

    private Set<DirectorDto> directors;

    public Genre addGenre(Genre genre) {
        if (genre == null) {
            return genre;
        }
        this.genres.stream()
                .filter(genreDto -> genreDto.getId().equals(genre.getId()))
                .forEach(genreDto -> genreDto.setName(genre.getName()));
        return genre;
    }

    public Director addDirector(Director director) {
        if (director == null) {
            return director;
        }

        this.directors.stream()
                .filter(directorDto -> directorDto.getId().equals(director.getId()))
                .forEach(directorDto -> directorDto.setName(director.getName()));
        return director;
    }

    public Mpa addMpa(Mpa mpa) {
        if (mpa == null) {
            return mpa;
        }
        this.mpa = MpaDto.builder()
                .id(mpa.getId())
                .name(mpa.getName())
                .build();
        return mpa;
    }
}
