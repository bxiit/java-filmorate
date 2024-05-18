package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder(toBuilder = true)
public class User {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("email")
    @NotNull(message = "Пустой адрес электронной почты")
    @Email(message = "Неверный формат электронной почты")
    private String email;

    @JsonProperty("login")
    @NotEmpty(message = "Пустой логин")
    private String login;

    @JsonProperty("name")
    private String name;

    @JsonProperty("birthday")
    @NotNull(message = "Пустая дата рождения")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthday;

    @JsonCreator()
    private static User decodeUser(
            @JsonProperty("id") Long id,
            @JsonProperty("email") String email,
            @JsonProperty("login") String login,
            @JsonProperty("name") String name,
            @JsonProperty("birthday") LocalDate birthday
    ) {
        return User.builder()
                .id(id)
                .login(login)
                .name(name)
                .email(email)
                .birthday(birthday)
                .build();
    }
}
