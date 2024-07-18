package ru.yandex.practicum.filmorate.dto.genre;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
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
