package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private static final HttpHeaders HTTP_HEADERS = new HttpHeaders();
    private final UserService userService;

    static {
        HTTP_HEADERS.setContentType(APPLICATION_JSON);
    }

    @PostMapping()
    public ResponseEntity<User> addUser(@Valid @RequestBody User user) {
        User addedUser = userService.addUser(user);
        return new ResponseEntity<>(addedUser, HTTP_HEADERS, CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long userID) {
        User user = userService.findUserById(userID)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return new ResponseEntity<>(user, HTTP_HEADERS, OK);
    }

    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.findAllUsers();
        return new ResponseEntity<>(allUsers, HTTP_HEADERS, OK);
    }

    @PutMapping()
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        return new ResponseEntity<>(updatedUser, HTTP_HEADERS, OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable("id") long userID) {
        userService.deleteUserById(userID);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
