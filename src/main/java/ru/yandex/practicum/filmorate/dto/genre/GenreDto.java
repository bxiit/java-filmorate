package ru.yandex.practicum.filmorate.dto.genre;

import jakarta.annotation.Nullable;
import lombok.Data;

@Data
public class GenreDto implements Comparable<GenreDto> {
    private Long id;
    private String name;

    @Override
    public int compareTo(@Nullable GenreDto genreDto) {
        if (genreDto == null) {
            return 0;
        }
        return Long.compare(this.id, genreDto.getId());
    }
}
