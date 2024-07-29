package ru.yandex.practicum.filmorate.service.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.ConflictException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.friend.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final FilmService filmService;

    public UserServiceImpl(UserStorage userStorage, FriendStorage friendStorage, FilmService filmService) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
        this.filmService = filmService;
    }

    @Override
    public UserDto addUser(NewUserRequest request) {
        User user = UserMapper.MAPPER.mapNewUserToUser(request);
        user = userStorage.addUser(user);
        return UserMapper.MAPPER.mapToUserDto(user);
    }

    @Override
    public UserDto findUserById(long userId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        setFriendsByUser(userId, user);
        return UserMapper.MAPPER.mapToUserDto(user);
    }

    private User setFriendsByUser(long userId, User user) {
        Set<Long> friendsIds = friendStorage.findFriendsById(userId).stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        user.setFriends(friendsIds);
        return user;
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userStorage.findAllUsers().stream()
                .map(user -> setFriendsByUser(user.getId(), user))
                .map(UserMapper.MAPPER::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto updateUser(long userId, UpdateUserRequest request) {
        User updatedUser = userStorage.findUserById(userId)
                .map(user -> UserMapper.MAPPER.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        updatedUser = userStorage.updateUser(updatedUser);
        return UserMapper.MAPPER.mapToUserDto(updatedUser);
    }

    @Override
    public void deleteUserById(long userId) {
        userStorage.deleteUserById(userId);
    }


    @Override// USER'S FRIENDS
    public void addUsersFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new ConflictException("Вы не можете добавить себя же в друзья");
        }
        doesUsersExist(userId);
        doesUsersExist(friendId);
        String reqStatus = setStatus(userId, friendId);
        if (userId < friendId) {
            friendStorage.addFriend(userId, friendId, reqStatus);
        } else {
            friendStorage.addFriend(friendId, userId, reqStatus);
        }
    }

    @Override
    public void deleteUsersFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new ConflictException("вы не можете добавить себя же в друзья");
        }
        doesUsersExist(userId);
        doesUsersExist(friendId);
        String reqStatus = setStatus(userId, friendId);
        if (userId < friendId) {
            friendStorage.deleteFriend(userId, friendId, reqStatus);
        } else {
            friendStorage.deleteFriend(friendId, userId, reqStatus);
        }
    }

    @Override
    public List<UserDto> findUsersFriends(long userId) {
        if (userStorage.findUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        return friendStorage.findFriendsById(userId).stream()
                .map(UserMapper.MAPPER::mapToUserDto)
                .toList();
    }

    @Override
    public List<UserDto> findCommonUsers(long userId, long otherUserId) {
        return friendStorage.findCommonFriends(userId, otherUserId).stream()
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

    @Override
    public List<FilmDto> getFilmRecommendations(long userId) {
        UserDto userDto = findUserById(userId); //Проверка, что пользователь существует
        return filmService.getFilmRecommendations(userId);
    }
}
