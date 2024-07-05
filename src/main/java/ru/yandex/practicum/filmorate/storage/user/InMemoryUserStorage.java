package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Qualifier("inMemory")
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public Optional<User> findUserById(long userId) {
        return Optional.ofNullable(users.get(userId));
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
    public boolean deleteUserById(long userId) {
        User removedUser = users.remove(userId);
        return removedUser.getId() == userId;
    }

    @Override
    public Long addFriend(long user1Id, long user2Id, String reqStatus) {
        return 0L;
    }

    @Override
    public List<User> findFriendsById(long userId) {
        // todo: no need
        return List.of();
    }

    @Override
    public List<User> findCommonFriends(long userId, long friendId) {
        // todo: no need
        return List.of();
    }

    @Override
    public boolean deleteFriend(long user1Id, long user2Id, String reqStatus) {
        return false;
    }
}
