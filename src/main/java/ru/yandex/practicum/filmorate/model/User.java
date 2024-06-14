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
import java.util.HashSet;
import java.util.Set;

@Data
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

    @JsonCreator()
    private static User userJson(
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
                .friends(new HashSet<>())
                .build();
    }
}
