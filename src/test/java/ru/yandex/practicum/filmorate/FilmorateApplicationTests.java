package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.assertj.core.data.Index;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDBStorage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class FilmorateApplicationTests {

    private final JdbcTemplate jdbcTemplate;

    private final UserDBStorage userStorage;

    @SneakyThrows({SQLException.class, NullPointerException.class})
    @BeforeEach
    void clearDatabase() {
        ClassPathResource schemaResource = new ClassPathResource("schema.sql");
        ClassPathResource dataResource = new ClassPathResource("data.sql");
        ScriptUtils.executeSqlScript(
                Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection(),
                schemaResource);
        ScriptUtils.executeSqlScript(Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection(),
                dataResource);
    }

    @Test
    void contextLoads() {
    }

    @Test
    void testAddUser() {
        User userToAdd = User.builder()
                .name("Bexeiit")
                .email("atabekbekseiit@gmail.com")
                .login("bxiit")
                .birthday(LocalDate.now())
                .build();
        User addedUser = userStorage.addUser(userToAdd);
        assertThat(addedUser)
                .hasFieldOrProperty("id");
    }

    @Test
    void testAddSameUserTwice() {
        User sameUser = User.builder()
                .name("sameUser")
                .email("sameUser@gmail.com")
                .login("same_user")
                .birthday(LocalDate.now())
                .build();
        userStorage.addUser(sameUser);
        assertThat(sameUser)
                .hasNoNullFieldsOrPropertiesExcept("friends");

        assertThatThrownBy(() -> userStorage.addUser(sameUser))
                .isInstanceOf(DuplicateKeyException.class)
                .satisfies(exception -> {
                    DuplicateKeyException duplicateKeyException = (DuplicateKeyException) exception;

                    if (duplicateKeyException.getRootCause() instanceof JdbcSQLIntegrityConstraintViolationException jdbcException) {
                        int duplicateErrorCode = jdbcException.getErrorCode();

                        assertThat(duplicateErrorCode)
                                .isEqualTo(23505);
                    }
                });
    }

    @Test
    void testFindUserById() {
        User userToAdd = User.builder()
                .name("Bexeiit")
                .email("atabekbekseiit@gmail.com")
                .login("bxiit")
                .birthday(LocalDate.now())
                .build();
        userStorage.addUser(userToAdd);
        Optional<User> userOptional = userStorage.findUserById(1);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u).hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    void testUpdateUser() {
        User userToAddThenUpdate = User.builder()
                .name("BexeiitToUpdate")
                .email("atabekbekseiittoupdate@gmail.com")
                .login("bxiit_to_update")
                .birthday(LocalDate.now())
                .build();
        userToAddThenUpdate = userStorage.addUser(userToAddThenUpdate);

        long addedUserId = userToAddThenUpdate.getId();

        User updatedUser = userToAddThenUpdate.toBuilder()
                .name("BexeiitUpdated")
                .email("atabekbekseiittoupdate@gmail.com")
                .login("bxiit_to_update")
                .build();
        userStorage.updateUser(updatedUser);


        Optional<User> updatedUserOptional = userStorage.findUserById(addedUserId);

        assertThat(updatedUserOptional)
                .isPresent()
                .hasValueSatisfying(
                        u -> {
                            assertThat(u).hasFieldOrPropertyWithValue("name", "BexeiitUpdated");
                            assertThat(u).hasFieldOrPropertyWithValue("email", "atabekbekseiittoupdate@gmail.com");
                            assertThat(u).hasFieldOrPropertyWithValue("login", "bxiit_to_update");
                        }
                );
    }

    @Test
    void testDeleteUser() {
        User userToAddThenDelete = User.builder()
                .name("BexeiitToDelete")
                .email("atabekbekseiittodelete@gmail.com")
                .login("bxiit_to_delete")
                .birthday(LocalDate.now())
                .build();
        userToAddThenDelete = userStorage.addUser(userToAddThenDelete);

        long addedUserId = userToAddThenDelete.getId();
        boolean deleted = userStorage.deleteUserById(addedUserId);

        assertThat(deleted)
                .isTrue();
    }

    @Test
    void testFindDeletedUser() {
        final User userToAddThenDeleteThenFind = User.builder()
                .name("BexeiitToDeleteThenFind")
                .email("atabekbekseiittodeletethenfind@gmail.com")
                .login("bxiit_to_delete_then_find")
                .birthday(LocalDate.now())
                .build();
        userStorage.addUser(userToAddThenDeleteThenFind);

        long userId = userToAddThenDeleteThenFind.getId();

        Optional<User> userOptional = userStorage.findUserById(userId);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(
                        user -> assertThat(user).isEqualTo(userToAddThenDeleteThenFind)
                );

        boolean deleted = userStorage.deleteUserById(userId);

        assertThat(deleted)
                .isTrue();

        Optional<User> deletedUserFinding = userStorage.findUserById(userId);
        assertThat(deletedUserFinding)
                .isEmpty();
    }

    @Test
    void testFindUserFriends() {
        User user1 = User.builder()
                .name("User1")
                .email("user1@gmail.com")
                .login("user1")
                .birthday(LocalDate.now())
                .build();
        user1 = userStorage.addUser(user1);

        User user2 = User.builder()
                .name("User2")
                .email("user2@gmail.com")
                .login("user2")
                .birthday(LocalDate.now())
                .build();
        user2 = userStorage.addUser(user2);

        long friendshipId = userStorage.addFriend(
                user1.getId(),
                user2.getId(),
                "REQ_USER1"
        );

        assertThat(friendshipId)
                .isGreaterThan(0L);

        List<User> friendsById = userStorage.findFriendsById(user1.getId());
        assertThat(friendsById)
                .isEqualTo(List.of(user2))
                .contains(user2, Index.atIndex(0));
    }
}
