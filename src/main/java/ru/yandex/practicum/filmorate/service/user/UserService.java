package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConflictException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.SPACE;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserStorage userStorage;

    public User addUser(User user) {
        validateUser(user);
        user.setId(getMaxId());
        user.setFriends(new HashSet<>());
        if (Objects.isNull(user.getName())) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User findUserById(long userId) {
        return userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User updateUser(User userWithNewData) {
        log.debug("Обновление пользователя с id: {}", userWithNewData.getId());

        validateUser(userWithNewData);

        User userWithOldData = userStorage.findUserById(userWithNewData.getId())
                .orElseThrow(() -> {
                    log.error("Пользователь с id: {} не найден", userWithNewData.getId());
                    return new NotFoundException("Не существует пользователя с id: " + userWithNewData.getId());
                });

        if (userWithNewData.getName() != null) {
            userWithOldData.setName(userWithNewData.getName());
        }
        if (userWithNewData.getLogin() != null) {
            userWithOldData.setLogin(userWithNewData.getLogin());
        }
        if (userWithNewData.getEmail() != null) {
            userWithOldData.setEmail(userWithNewData.getEmail());
        }
        if (userWithNewData.getBirthday() != null) {
            userWithOldData.setBirthday(userWithNewData.getBirthday());
        }

        User updatedUser = userStorage.updateUser(userWithNewData);
        log.info("Пользователь успешно обновлен с ID: {}", updatedUser.getId());
        return updatedUser;
    }

    public void deleteUserById(long userId) {
        userStorage.deleteUserById(userId);
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
        return IdGenerator.getMaxIdOfUsers();
    }

    // USER'S FRIENDS
    public void addUsersFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new ConflictException("вы не можете добавить себя же в друзья");
        }

        User user = findUserById(userId);

        User userFriend = findUserById(friendId);

        user.getFriends().add(userFriend.getId());
        userFriend.getFriends().add(user.getId());
    }

    public void deleteUsersFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new ConflictException("вы не можете добавить себя же в друзья");
        }
        User user = findUserById(userId);
        User userFriend = findUserById(friendId);

        user.getFriends().remove(userFriend.getId());
        userFriend.getFriends().remove(user.getId());
    }

    public List<User> findUsersFriends(long userId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь не найден"));

        List<User> userFriends = user.getFriends().stream()
                .map(userStorage::findUserById)
                .map(optionalFriend -> optionalFriend.orElseThrow(() -> new NotFoundException("пользователь не найден")))
                .toList();

        return userFriends;
    }

    public List<User> findCommonUsers(long userId, long otherUserId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException("пользователь не найден"));
        List<User> usersCommonFriends = user.getFriends().stream()
                .map(userStorage::findUserById)
                .map(optionalUser -> optionalUser.orElseThrow(() -> new NotFoundException("пользователь не найден")))
                .filter(userFriend -> userFriend.getFriends().contains(otherUserId))
                .toList();

        return usersCommonFriends;
    }
}
