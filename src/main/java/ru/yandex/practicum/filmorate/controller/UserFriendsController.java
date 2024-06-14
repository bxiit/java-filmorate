package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.List;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{id}/friends")
public class UserFriendsController {
    private final UserService userService;

    @PutMapping("/{friendId}")
    public ResponseEntity<Void> addUserFriends(
            @PathVariable("id") long userID,
            @PathVariable("friendId") long friendID
    ) {
        userService.addUsersFriend(userID, friendID);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> deleteUsersFriend(
            @PathVariable("id") long userID,
            @PathVariable("friendId") long friendID
    ) {
        userService.deleteUsersFriend(userID, friendID);
        return new ResponseEntity<>(NO_CONTENT);
    }

    @GetMapping()
    public ResponseEntity<List<User>> getUsersFriends(@PathVariable("id") long userID) {
        List<User> usersFriends = userService.findUsersFriends(userID);
        return new ResponseEntity<>(usersFriends, OK);
    }

    @GetMapping("/common/{otherId}")
    public ResponseEntity<List<User>> getCommonFriends(
            @PathVariable("id") long userID,
            @PathVariable("otherId") long otherUserID
    ) {
        List<User> commonFriends = userService.findCommonUsers(userID, otherUserID);
        return new ResponseEntity<>(commonFriends, OK);
    }
}

