package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("userDBStorage")
//@Primary
public class UserDBStorage extends BaseRepository<User> implements UserStorage {
    private static final String INSERT_QUERY = """
            INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY)
            VALUES (?, ?, ?, ?)
            """;
    private static final String FIND_BY_ID_QUERY = """
            SELECT * FROM USERS WHERE USER_ID = ?
            """;
    private static final String FIND_ALL_QUERY = "SELECT * FROM USERS";

    private static final String UPDATE_QUERY = """
            UPDATE USERS
            SET NAME = ?, LOGIN = ?, EMAIL = ?, BIRTHDAY = ?
            WHERE USER_ID = ?
            """;
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM USERS WHERE USER_ID = ?";

    @Autowired
    public UserDBStorage(JdbcTemplate jdbc, RowMapper<User> rowMapper, ResultSetExtractor<List<User>> extractor) {
        super(jdbc, rowMapper, extractor);
    }

    @Override
    public User addUser(User user) {
        long id = 0;
        try {
            id = insert(
                    INSERT_QUERY,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    user.getBirthday()
            );
        } catch (DataIntegrityViolationException e) {
            handleAlreadyExistence("Пользователь уже существует", e);
        }
        user.setId(id);
        return user;
    }

    @Override
    public Optional<User> findUserById(long userId) {
        return findOne(FIND_BY_ID_QUERY, userId);
    }

    @Override
    public List<User> findAllUsers() {
        return findMany(FIND_ALL_QUERY);
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
}
