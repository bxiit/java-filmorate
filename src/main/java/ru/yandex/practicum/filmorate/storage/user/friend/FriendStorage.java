package ru.yandex.practicum.filmorate.storage.user.friend;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {
    List<User> findFriendsById(long userId);

    List<User> findCommonFriends(long userId, long friendId);

    Long addFriend(long user1Id, long user2Id, String reqStatus);

    boolean deleteFriend(long user1Id, long user2Id, String reqStatus);
}
