package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public Optional<User> findUserById(long userID) {
        return Optional.ofNullable(users.get(userID));
    }

    @Override
    public List<User> findAllUsers() {
        return users.values().stream().toList();
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public boolean deleteUserById(long userID) {
        User removedUser = users.remove(userID);
        return removedUser.getId() == userID;
    }
}
