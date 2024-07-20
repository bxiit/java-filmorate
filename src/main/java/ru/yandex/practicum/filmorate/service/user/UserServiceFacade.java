package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.service.event.EventService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserServiceFacade {
    private final UserService userService;
    private final EventService eventService;

    public UserDto addUser(NewUserRequest request) {
        return userService.addUser(request);
    }

    public UserDto findUserById(long userId) {
        return userService.findUserById(userId);
    }

    public List<UserDto> findAllUsers() {
        return userService.findAllUsers();
    }

    public UserDto updateUser(long userId, UpdateUserRequest request) {
        return userService.updateUser(userId, request);
    }

    public void deleteUserById(long userId) {
        userService.deleteUserById(userId);
    }

    // USER'S FRIENDS
    public void addUsersFriend(long userId, long friendId) {
        userService.addUsersFriend(userId, friendId);
        eventService.addEvent(userId, friendId, EventType.FRIEND, Operation.ADD);
    }

    public void deleteUsersFriend(long userId, long friendId) {
        userService.deleteUsersFriend(userId, friendId);
        eventService.addEvent(userId, friendId, EventType.FRIEND, Operation.REMOVE);
    }

    public List<UserDto> findUsersFriends(long userId) {
        return userService.findUsersFriends(userId);
    }

    public List<UserDto> findCommonUsers(long userId, long otherUserId) {
        return userService.findCommonUsers(userId, otherUserId);
    }

    public List<FilmDto> getFilmRecommendations(long userId) {
        return userService.getFilmRecommendations(userId);
    }
}
