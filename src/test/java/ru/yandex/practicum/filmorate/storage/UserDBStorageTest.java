package ru.yandex.practicum.filmorate.storage;

import org.assertj.core.data.Index;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.exception.ConflictException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDBStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserDBStorageTest extends BaseDBStorageTest<UserDBStorage> {

    @Autowired
    public UserDBStorageTest(UserDBStorage userDBStorage) {
        super(userDBStorage);
    }

    @Test
    void testAddUser() {
        User userToAdd = User.builder()
                .name("Bexeiit")
                .email("atabekbekseiit@gmail.com")
                .login("bxiit")
                .birthday(LocalDate.now())
                .build();
        User addedUser = storage.addUser(userToAdd);
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
        storage.addUser(sameUser);
        assertThat(sameUser)
                .hasNoNullFieldsOrPropertiesExcept("friends");

        assertThatThrownBy(() -> storage.addUser(sameUser))
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void testFindUserById() {
        User userToAdd = User.builder()
                .name("Bexeiit")
                .email("atabekbekseiit@gmail.com")
                .login("bxiit")
                .birthday(LocalDate.now())
                .build();
        storage.addUser(userToAdd);
        Optional<User> userOptional = storage.findUserById(1);

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
        userToAddThenUpdate = storage.addUser(userToAddThenUpdate);

        long addedUserId = userToAddThenUpdate.getId();

        User updatedUser = userToAddThenUpdate.toBuilder()
                .name("BexeiitUpdated")
                .email("atabekbekseiittoupdate@gmail.com")
                .login("bxiit_to_update")
                .build();
        storage.updateUser(updatedUser);


        Optional<User> updatedUserOptional = storage.findUserById(addedUserId);

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
        userToAddThenDelete = storage.addUser(userToAddThenDelete);

        long addedUserId = userToAddThenDelete.getId();
        boolean deleted = storage.deleteUserById(addedUserId);

        assertThat(deleted)
                .isTrue();
    }

    @Test
    void testFindDeletedUser() {
        LocalDate birthday = LocalDate.now();
        final User userToAddThenDeleteThenFind = User.builder()
                .name("BexeiitToDeleteThenFind")
                .email("atabekbekseiittodeletethenfind@gmail.com")
                .login("bxiit_to_delete_then_find")
                .birthday(birthday)
                .friends(new HashSet<>())
                .build();
        storage.addUser(userToAddThenDeleteThenFind);

        long userId = userToAddThenDeleteThenFind.getId();

        Optional<User> userOptional = storage.findUserById(userId);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(
                        user -> {
                            assertThat(user.getEmail()).isEqualTo("atabekbekseiittodeletethenfind@gmail.com");
                            assertThat(user.getName()).isEqualTo("BexeiitToDeleteThenFind");
                            assertThat(user.getBirthday()).isEqualTo(birthday);
                            assertThat(user.getLogin()).isEqualTo("bxiit_to_delete_then_find");
                        }
                );

        boolean deleted = storage.deleteUserById(userId);

        assertThat(deleted)
                .isTrue();

        Optional<User> deletedUserFinding = storage.findUserById(userId);
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
        user1 = storage.addUser(user1);

        User user2 = User.builder()
                .name("User2")
                .email("user2@gmail.com")
                .login("user2")
                .birthday(LocalDate.now())
                .build();
        user2 = storage.addUser(user2);

        long friendshipId = storage.addFriend(
                user1.getId(),
                user2.getId(),
                "REQ_USER1"
        );

        assertThat(friendshipId)
                .isGreaterThan(0L);

        List<User> friendsById = storage.findFriendsById(user1.getId());
        assertThat(friendsById)
                .isEqualTo(List.of(user2))
                .contains(user2, Index.atIndex(0));
    }
}
