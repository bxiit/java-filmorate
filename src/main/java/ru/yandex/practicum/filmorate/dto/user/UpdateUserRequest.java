package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {
    private long id;

    @Email(message = "Неверный формат электронной почты")
    private String email;

    @NotEmpty(message = "Пустой логин")
    private String login;

    @NotEmpty(message = "Пустое имя")
    private String name;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Past(message = "Некорректная дата дня рождения")
    private LocalDate birthday;

    public boolean hasEmail() {
        return this.getEmail() != null;
    }

    public boolean hasLogin() {
        return this.getLogin() != null;
    }

    public boolean hasName() {
        return this.getName() != null;
    }

    public boolean hasBirthday() {
        return this.getBirthday() != null;
    }
}
