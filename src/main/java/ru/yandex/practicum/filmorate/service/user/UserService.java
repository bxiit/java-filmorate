package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.ConflictException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Qualifier("UserDBStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto addUser(NewUserRequest request) {
        User userTest = UserMapper.MAPPER.mapNewUserToUser(request);
        userTest = userStorage.addUser(userTest);
        return UserMapper.MAPPER.mapToUserDto(userTest);
    }

    public UserDto findUserById(long userId) {
        return userStorage.findUserById(userId)
                .map(UserMapper.MAPPER::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public List<UserDto> findAllUsers() {
        return userStorage.findAllUsers()
                .stream().map(UserMapper.MAPPER::mapToUserDto)
                .toList();
    }

    public UserDto updateUser(long userId, UpdateUserRequest request) {
        User updatedUser = userStorage.findUserById(userId)
                .map(user -> UserMapper.MAPPER.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updatedUser = userStorage.updateUser(updatedUser);
        return UserMapper.MAPPER.mapToUserDto(updatedUser);
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
                .map(UserMapper.MAPPER::mapToUserDto)
                .toList();
    }

    public List<UserDto> findCommonUsers(long userId, long otherUserId) {
        return userStorage.findCommonFriends(userId, otherUserId).stream()
                .map(UserMapper.MAPPER::mapToUserDto)
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
}
