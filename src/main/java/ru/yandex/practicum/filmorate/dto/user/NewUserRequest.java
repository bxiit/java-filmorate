package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.util.annotations.NoSpace;

import java.time.LocalDate;

@Data
public class NewUserRequest {

    private String name;

    @NotNull(message = "Пустой адрес электронной почты")
    @Email(message = "Неверный формат электронной почты")
    private String email;

    @NotEmpty(message = "Пустой логин")
    @NoSpace(message = "Логин содержит пробел")
    private String login;


    @NotNull(message = "Пустая дата рождения")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @PastOrPresent(message = "Некорректная дата дня рождения")
    private LocalDate birthday;

    public String getName() {
        if (name != null) {
            return name.trim().isEmpty() ? login : name;
        } else {
            throw new ValidationException("Имя не должно быть пустым.");
        }
    }
}
