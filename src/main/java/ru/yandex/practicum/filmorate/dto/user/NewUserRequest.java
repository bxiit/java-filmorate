package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.util.validator.annotations.NoSpace;

import java.time.LocalDate;

@Getter
@Setter
public class NewUserRequest {

    @NotNull(message = "Пустой адрес электронной почты")
    @Email(message = "Неверный формат электронной почты")
    private String email;

    @NotEmpty(message = "Пустой логин")
    @NoSpace(message = "Логин содержит пробел")
    private String login;

    private String name;

    @NotNull(message = "Пустая дата рождения")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Past(message = "Некорректная дата дня рождения")
    private LocalDate birthday;
}
