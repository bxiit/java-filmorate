package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    Optional<User> findUserById(long userId);

    List<User> findAllUsers();

    User updateUser(User user);

    boolean deleteUserById(long userId);
}
