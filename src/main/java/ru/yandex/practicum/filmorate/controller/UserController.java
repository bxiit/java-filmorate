package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j()
public class UserController {
    public static final String SPACE = " ";
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping()
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        log.info("Создание нового пользователя");
        validateLoginAndBirthDate(user);
        user.setId(getMaxId());
        if (Objects.isNull(user.getName())) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан с ID: {}", user.getId());
        return ResponseEntity.of(Optional.of(users.get(user.getId())));
    }

    @PutMapping()
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.debug("Обновление пользователя с id: {}", user.getId());
        validateLoginAndBirthDate(user);
        User userToUpdate = Optional.ofNullable(users.get(user.getId()))
                .orElseThrow(() -> {
                    log.error("Пользователь с id: {} не найден", user.getId());
                    return new NotFoundException("Не существует пользователя с id: " + user.getId());
                });
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getLogin() != null) {
            userToUpdate.setLogin(user.getLogin());
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        users.put(userToUpdate.getId(), userToUpdate);
        log.info("Пользователь успешно обновлен с ID: {}", user.getId());

        return ResponseEntity.of(Optional.of(users.get(user.getId())));
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.of(Optional.of(new ArrayList<>(users.values())));
    }

    private void validateLoginAndBirthDate(User user) {
        log.warn("Валидация пользователя с email {}", user.getEmail());
        log.warn("Валидация почты {}", user.getEmail());
        log.warn("Валидация даты рождения {}", user.getBirthday());
        String userLogin = user.getLogin();
        if (userLogin.contains(SPACE)) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Не валидная дата рождения");
        }
    }

    private Long getMaxId() {
        return IdGenerator.getMaxIdOfMap(users.keySet());
    }
}
