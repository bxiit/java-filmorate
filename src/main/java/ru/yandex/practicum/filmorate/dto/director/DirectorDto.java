package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class DirectorDto {

    @Positive
    private Long id;

    @NotEmpty(message = "Имя режиссера не должен быть пустым")
    private String name;
}
