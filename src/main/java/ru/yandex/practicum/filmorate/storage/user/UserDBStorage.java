package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.AlreadyDoneException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("UserDBStorage")
@Primary
public class UserDBStorage extends BaseRepository<User> implements UserStorage {
    private static final String INSERT_QUERY = """
            INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
            VALUES (?, ?, ?, ?)
            """;
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM USERS WHERE USER_ID = ?";
    private static final String FIND_ALL_QUERY = "SELECT * FROM USERS";
    private static final String FIND_USER_FRIENDS = """
            SELECT *
            FROM USERS
            WHERE USER_ID IN (SELECT (CASE
                                          WHEN user1_id = ? THEN user2_id
                                          ELSE user1_id
                END) AS friend_id
                              FROM FRIENDS
                              WHERE (user1_id = ? OR user2_id = ?) AND ((? < USER2_ID AND STATUS = 'REQ_USER1')
                                  OR (? > USER1_ID AND STATUS = 'REQ_USER2')));
            """;
    private static final String FIND_COMMON_FRIENDS_QUERY = """
            SELECT *
            FROM USERS
            WHERE USER_ID IN (SELECT (CASE
                                          WHEN user1_id = ? THEN user2_id
                                          ELSE user1_id
                END) AS friend_id
                              FROM FRIENDS
                              WHERE (user1_id = ? OR user2_id = ?) AND ((? < USER2_ID AND STATUS = 'REQ_USER1')
                                  OR (? > USER1_ID AND STATUS = 'REQ_USER2')))
            INTERSECT
            SELECT *
            FROM USERS
            WHERE USER_ID IN (SELECT (CASE
                                          WHEN user1_id = ? THEN user2_id
                                          ELSE user1_id
                END) AS friend_id
                              FROM FRIENDS
                              WHERE (user1_id = ? OR user2_id = ?) AND ((? < USER2_ID AND STATUS = 'REQ_USER1')
                                  OR (? > USER1_ID AND STATUS = 'REQ_USER2')));
            """;

    private static final String UPDATE_QUERY = """
            UPDATE USERS
            SET NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ?
            WHERE USER_ID = ?
            """;
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM USERS WHERE USER_ID = ?";
    private static final String ADD_FRIEND_QUERY = """
            INSERT INTO FRIENDS (USER1_ID, USER2_ID, STATUS)
            VALUES ( ?, ?, ? );
            """;
    private static final String DELETE_FRIEND_QUERY = """
            DELETE FROM FRIENDS WHERE (USER1_ID = ?) AND (USER2_ID = ?) AND (STATUS = ?);
            """;

    @Autowired
    public UserDBStorage(JdbcTemplate jdbc, RowMapper<User> rowMapper, ResultSetExtractor<List<User>> extractor) {
        super(jdbc, rowMapper, extractor);
    }

    @Override
    public User addUser(User user) {
        long id = insert(
                INSERT_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public Optional<User> findUserById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    @Override
    public List<User> findFriendsById(long userId) {
        return findMany(FIND_USER_FRIENDS, userId, userId, userId, userId, userId);
    }

    @Override
    public List<User> findAllUsers() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public List<User> findCommonFriends(long userId, long friendId) {
        return findMany(
                FIND_COMMON_FRIENDS_QUERY, userId, userId, userId, userId, userId,
                friendId, friendId, friendId, friendId, friendId);
    }

    @Override
    public User updateUser(User user) {
        update(
                UPDATE_QUERY,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public boolean deleteUserById(long userId) {
        return delete(DELETE_BY_ID_QUERY, userId);
    }

    @Override
    public Long addFriend(long user1Id, long user2Id, String reqStatus) {
        try {
            return insert(ADD_FRIEND_QUERY, user1Id, user2Id, reqStatus);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyDoneException("Уже в друзьях");
        } catch (EmptyResultDataAccessException ignored) {
            throw new NotFoundException("");
        }
    }

    @Override
    public boolean deleteFriend(long user1Id, long user2Id, String reqStatus) {
        return delete(DELETE_FRIEND_QUERY, user1Id, user2Id, reqStatus);
    }
}
