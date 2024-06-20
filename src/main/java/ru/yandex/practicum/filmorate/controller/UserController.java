package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        log.info("Создание нового пользователя");
        User addedUser = userService.addUser(user);
        log.info("Пользователь успешно создан с ID: {}", addedUser.getId());
        return new ResponseEntity<>(addedUser, CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long userId) {
        User user = userService.findUserById(userId);
        log.info("Получение пользователя с ID = {}", userId);
        return new ResponseEntity<>(user, OK);
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.findAllUsers();
        log.info("Получение всех пользователей");
        return new ResponseEntity<>(allUsers, OK);
    }

    @PutMapping()
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        log.info("Обновление пользователя с ID = {}", user.getId());
        User updatedUser = userService.updateUser(user);
        log.info("Пользователь с ID = {} успешно обновлен", user.getId());
        return new ResponseEntity<>(updatedUser, OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") long userId) {
        userService.deleteUserById(userId);
        log.info("Удаление пользователя с ID = {}", userId);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addUserFriends(
            @PathVariable("id") long userId,
            @PathVariable("friendId") long friendId
    ) {
        log.debug("Добавление пользователя с ID = {} в друзья пользователя с ID = {}", friendId, userId);
        userService.addUsersFriend(userId, friendId);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> deleteUsersFriend(
            @PathVariable("id") long userId,
            @PathVariable("friendId") long friendId
    ) {
        userService.deleteUsersFriend(userId, friendId);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getUsersFriends(@PathVariable("id") long userId) {
        List<User> usersFriends = userService.findUsersFriends(userId);
        return new ResponseEntity<>(usersFriends, OK);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(
            @PathVariable("id") long userId,
            @PathVariable("otherId") long otherUserId
    ) {
        log.info("Получение общих друзей пользователя с ID = {} и ID = {}", userId, otherUserId);
        List<User> commonFriends = userService.findCommonUsers(userId, otherUserId);
        return new ResponseEntity<>(commonFriends, OK);
    }
}
