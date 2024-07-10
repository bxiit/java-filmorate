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
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.FriendsStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendsRepository;
import ru.yandex.practicum.filmorate.storage.user.UserRepository;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.friend.FriendStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendStorage friendStorage;
    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;

    public UserService(@Qualifier("UserDBStorage") UserStorage userStorage, @Qualifier("friendDBStorage") FriendStorage friendStorage, UserRepository userRepository, FriendsRepository friendsRepository) {
        this.userStorage = userStorage;
        this.friendStorage = friendStorage;
        this.userRepository = userRepository;
        this.friendsRepository = friendsRepository;
    }

    public UserDto addUser(NewUserRequest request) {
        User user = UserMapper.MAPPER.mapNewUserToUser(request);
        user = userRepository.save(user);
        return UserMapper.MAPPER.mapToUserDto(user);
    }

    public UserDto findUserById(long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        setFriendsByUser(userId, user);
        return UserMapper.MAPPER.mapToUserDto(user);
    }

    private User setFriendsByUser(long userId, User user) {
        List<User> userFriends = friendsRepository.findUserFriends(userId);
        Set<Long> friendsIds = userFriends.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        user.setFriends(friendsIds);
//        Set<Long> friendsIds = friendStorage.findFriendsById(userId).stream()
//                .map(User::getId)
//                .collect(Collectors.toSet());
//        user.setFriends(friendsIds);
        return user;
    }

    public List<UserDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper.MAPPER::mapToUserDto)
                .toList();
    }

    public UserDto updateUser(UpdateUserRequest request) {
        User updatedUser = userRepository.findById(request.getId())
                .map(user -> UserMapper.MAPPER.updateUserFields(user, request))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userRepository.save(updatedUser);
        return UserMapper.MAPPER.mapToUserDto(updatedUser);
    }

    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }

    // USER'S FRIENDS
    public void addUsersFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new ConflictException("Вы не можете добавить себя же в друзья");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        FriendsStatus reqStatus = setStatus(userId, friendId);

        Friends friends = new Friends();
        friends.setStatus(reqStatus);
        if (userId < friendId) {
            friends.setUser1(user);
            friends.setUser2(friend);
        } else {
            friends.setUser1(friend);
            friends.setUser2(user);
        }
        friendsRepository.save(friends);
    }

    public void deleteUsersFriend(long userId, long friendId) {
        if (userId == friendId) {
            throw new ConflictException("вы не можете удалить себя же из списка друзей");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

        FriendsStatus status = setStatus(userId, friendId);
        if (userId < friendId) {
            friendsRepository.deleteFriendsByUser1AndUser2AndStatus(user, friend, status);
//            friendStorage.deleteFriend(userId, friendId, reqStatus);
        } else {
            friendsRepository.deleteFriendsByUser1AndUser2AndStatus(friend, user, status);
//            friendStorage.deleteFriend(friendId, userId, reqStatus);
        }
    }

    public List<UserDto> findUsersFriends(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }

        return friendsRepository.findUserFriends(userId).stream()
                .map(UserMapper.MAPPER::mapToUserDto)
                .toList();
//        return friendStorage.findFriendsById(userId).stream()
//                .map(UserMapper.MAPPER::mapToUserDto)
//                .toList();
    }

    public List<UserDto> findCommonUsers(long userId, long otherUserId) {
        return friendsRepository.findCommonFriends(userId, otherUserId).stream()
                .map(UserMapper.MAPPER::mapToUserDto)
                .toList();
//        return friendStorage.findCommonFriends(userId, otherUserId).stream()
//                .map(UserMapper.MAPPER::mapToUserDto)
//                .toList();
    }

    private FriendsStatus setStatus(long userId, long friendId) {
        FriendsStatus reqStatus;
        if (userId < friendId) {
            reqStatus = FriendsStatus.REQ_USER1;
        } else {
            reqStatus = FriendsStatus.REQ_USER2;
        }
        return reqStatus;
    }

    private void doesUsersExist(long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}
