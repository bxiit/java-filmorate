package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    @ResponseStatus(OK)
    public UserDto addUser(@Valid @RequestBody NewUserRequest request) {
        log.info("Создание нового пользователя");
        UserDto addedUser = userService.addUser(request);
        log.info("Пользователь успешно создан с ID: {}", addedUser.getId());
        return addedUser;
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public UserDto getUserById(@PathVariable("id") long userId) {
        UserDto userDto = userService.findUserById(userId);
        log.info("Получение пользователя с ID = {}", userId);
        return userDto;
    }

    @GetMapping()
    @ResponseStatus(OK)
    public List<UserDto> getAllUsers() {
        List<UserDto> allUsers = userService.findAllUsers();
        log.info("Получение всех пользователей");
        return allUsers;
    }

    @PutMapping()
    @ResponseStatus(OK)
    public UserDto updateUser(@Valid @RequestBody UpdateUserRequest request) {
        log.info("Обновление пользователя");
        UserDto updatedUser = userService.updateUser(request.getId(), request);
        log.info("Пользователь с ID = {} успешно обновлен", request.getId());
        return updatedUser;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    public void deleteUserById(@PathVariable("id") long userId) {
        userService.deleteUserById(userId);
        log.info("Удаление пользователя с ID = {}", userId);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(OK)
    public List<UserDto> getUsersFriends(@PathVariable("id") long userId) {
        return userService.findUsersFriends(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(OK)
    public void addUserFriends(
            @PathVariable("id") long userId,
            @PathVariable("friendId") long friendId
    ) {
        log.debug("Добавление пользователя с ID = {} в друзья пользователя с ID = {}", friendId, userId);
        userService.addUsersFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(OK)
    public void deleteUsersFriend(
            @PathVariable("id") long userId,
            @PathVariable("friendId") long friendId
    ) {
        userService.deleteUsersFriend(userId, friendId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(OK)
    public List<UserDto> getCommonFriends(
            @PathVariable("id") long userId,
            @PathVariable("otherId") long otherUserId
    ) {
        log.info("Получение общих друзей пользователя с ID = {} и ID = {}", userId, otherUserId);
        return userService.findCommonUsers(userId, otherUserId);
    }

    @GetMapping("/{id}/recommendations")
    @ResponseStatus(OK)
    public List<FilmDto> getFilmRecommendations(@PathVariable("id") long userId) {
        return userService.getFilmRecommendations(userId);
    }
}
