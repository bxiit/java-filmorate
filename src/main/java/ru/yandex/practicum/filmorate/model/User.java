package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long id;

    private String email;

    private String login;

    private String name;

    private LocalDate birthday;

    private Set<Long> friends;
}
