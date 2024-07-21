package ru.yandex.practicum.filmorate.dto.mpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class MpaDto {
    private Long id;
    private String name;
}
