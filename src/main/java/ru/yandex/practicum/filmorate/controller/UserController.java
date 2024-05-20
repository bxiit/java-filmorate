package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();
    private static final String SPACE = " ";
    private static final HttpHeaders HTTP_HEADERS = new HttpHeaders();

    static {
        HTTP_HEADERS.setContentType(MediaType.APPLICATION_JSON);
    }

    @PostMapping()
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        log.info("Создание нового пользователя");
        validateUser(user);
        user.setId(getMaxId());
        if (Objects.isNull(user.getName())) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь успешно создан с ID: {}", user.getId());

        return new ResponseEntity<>(users.get(user.getId()), HTTP_HEADERS, HttpStatus.CREATED);
    }

    @PutMapping()
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.debug("Обновление пользователя с id: {}", user.getId());

        validateUser(user);

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
        if (user.getBirthday() != null) {
            userToUpdate.setBirthday(user.getBirthday());
        }
        users.put(userToUpdate.getId(), userToUpdate);
        log.info("Пользователь успешно обновлен с ID: {}", user.getId());

        return new ResponseEntity<>(users.get(user.getId()), HTTP_HEADERS, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.of(Optional.of(new ArrayList<>(users.values())));
    }

    private void validateUser(User user) {
        log.warn("Валидация пользователя с email {}", user.getEmail());

        String userLogin = user.getLogin();
        log.warn("Валидация логина {}", user.getLogin());
        if (userLogin.contains(SPACE) || user.getLogin().isEmpty()) {
            throw new ValidationException("Логин не может содержать пробелы или быть пустым");
        }

        log.warn("Валидация даты рождения {}", user.getBirthday());
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Не валидная дата рождения");
        }
    }

    private Long getMaxId() {
        return IdGenerator.getMaxIdOfMap(users.keySet());
    }
}
