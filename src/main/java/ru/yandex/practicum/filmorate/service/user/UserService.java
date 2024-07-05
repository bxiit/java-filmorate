package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.ConflictException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.SPACE;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Qualifier("UserDBStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto addUser(NewUserRequest request) {
        User user = UserMapper.mapToUser(request);
        user = userStorage.addUser(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto findUserById(long userId) {
        return userStorage.findUserById(userId)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public List<UserDto> findAllUsers() {
        return userStorage.findAllUsers()
                .stream().map(UserMapper::mapToUserDto)
                .toList();
    }

    public UserDto updateUser(long userId, UpdateUserRequest request) {
        User updatedUser = userStorage.findUserById(userId)
                .map(user -> UserMapper.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updatedUser = userStorage.updateUser(updatedUser);
        return UserMapper.mapToUserDto(updatedUser);
    }

    public void deleteUserById(long userId) {
        userStorage.deleteUserById(userId);
    }

    // USER'S FRIENDS
    public void addUsersFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new ConflictException("Вы не можете добавить себя же в друзья");
        }
        doesUsersExist(userId);
        doesUsersExist(friendId);
        String reqStatus = setStatus(userId, friendId);
        if (userId < friendId) {
            userStorage.addFriend(userId, friendId, reqStatus);
        } else {
            userStorage.addFriend(friendId, userId, reqStatus);
        }
    }

    public void deleteUsersFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new ConflictException("вы не можете добавить себя же в друзья");
        }
        doesUsersExist(userId);
        doesUsersExist(friendId);
        String reqStatus = setStatus(userId, friendId);
        if (userId < friendId) {
            userStorage.deleteFriend(userId, friendId, reqStatus);
        } else {
            userStorage.deleteFriend(friendId, userId, reqStatus);
        }
    }

    public List<UserDto> findUsersFriends(long userId) {
        if (userStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return userStorage.findFriendsById(userId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    public List<UserDto> findCommonUsers(long userId, long otherUserId) {
        return userStorage.findCommonFriends(userId, otherUserId).stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    private String setStatus(long userId, long friendId) {
        String reqStatus;
        if (userId < friendId) {
            reqStatus = "REQ_USER1";
        } else {
            reqStatus = "REQ_USER2";
        }
        return reqStatus;
    }

    private void doesUsersExist(long userId) {
        if (userStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
    }

    private void validateNewUser(NewUserRequest request) {
        log.warn("Валидация пользователя с email {}", request.getEmail());

        if (Objects.isNull(request.getName())) {
            request.setName(request.getLogin());
        }
        String userLogin = request.getLogin();
        log.warn("Валидация логина {}", request.getLogin());
        if (userLogin.contains(SPACE) || userLogin.isEmpty()) {
            throw new ValidationException("Логин не может содержать пробелы или быть пустым");
        }

        log.warn("Валидация даты рождения {}", request.getBirthday());
        if (request.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Не валидная дата рождения");
        }
    }

    private void validateUpdatedUser(UpdateUserRequest request) {
        log.warn("Валидация пользователя с email {}", request.getEmail());

        log.warn("Валидация логина {}", request.getLogin());
        if (request.getLogin().contains(SPACE) || request.getLogin().isEmpty()) {
            throw new ValidationException("Логин не может содержать пробелы или быть пустым");
        }

        log.warn("Валидация даты рождения {}", request.getBirthday());
        if (request.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Не валидная дата рождения");
        }
    }
}
