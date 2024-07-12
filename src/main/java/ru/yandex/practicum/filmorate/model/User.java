package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
@Builder(toBuilder = true)
public class User {
    private Long id;

    @NotNull(message = "Пустой адрес электронной почты")
    @Email(message = "Неверный формат электронной почты")
    private String email;

    @NotEmpty(message = "Пустой логин")
    private String login;

    private String name;

    @NotNull(message = "Пустая дата рождения")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

    private Set<Long> friends;
}
