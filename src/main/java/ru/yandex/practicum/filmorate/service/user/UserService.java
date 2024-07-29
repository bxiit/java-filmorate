package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(NewUserRequest request);

    UserDto findUserById(long userId);

    List<UserDto> findAllUsers();

    UserDto updateUser(long userId, UpdateUserRequest request);

    void deleteUserById(long userId);

    void addUsersFriend(long userId, long friendId);

    void deleteUsersFriend(long userId, long friendId);

    List<UserDto> findUsersFriends(long userId);

    List<UserDto> findCommonUsers(long userId, long otherUserId);

    List<FilmDto> getFilmRecommendations(long userId);
}
