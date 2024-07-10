package ru.yandex.practicum.filmorate.storage.friends;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.model.FriendsStatus;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendsRepository extends JpaRepository<Friends, Long> {

    @Query("""

                        SELECT u
                        FROM User u
                        WHERE u.id IN (SELECT (CASE
                                                      WHEN f.user1.id = :userId THEN f.user2.id
                                                      ELSE f.user1.id
                            END) AS friend_id
                                          FROM Friends f
                                          WHERE (f.user1.id = :userId OR f.user2.id = :userId) AND ((:userId < f.user2.id AND f.status = 'REQ_USER1')
                                              OR (:userId > f.user1.id AND f.status = 'REQ_USER2')))
            """)
    List<User> findUserFriends(Long userId);

    // DELETE FROM FRIENDS WHERE (USER1_ID = ?) AND (USER2_ID = ?) AND (STATUS = ?);
    void deleteFriendsByUser1AndUser2AndStatus(User user1, User user2, FriendsStatus status);

    @Query("""
        SELECT u
            FROM User u
            WHERE u.id IN (SELECT (CASE
                                          WHEN f.user1.id = :userId THEN f.user2.id
                                          ELSE f.user1.id
                END) AS friend_id
                              FROM Friends f
                              WHERE (f.user1.id = :userId OR f.user2.id = :userId) AND ((:userId < f.user2.id AND f.status = 'REQ_USER1')
                                  OR (:userId > f.user1.id AND f.status = 'REQ_USER2')))
            INTERSECT
            SELECT u
            FROM User u
            WHERE u.id IN (SELECT (CASE
                                          WHEN f.user1.id = :otherUserId THEN f.user2.id
                                          ELSE f.user1.id
                END) AS friend_id
                              FROM Friends f
                              WHERE (f.user1.id = :otherUserId OR f.user2.id = :otherUserId) AND ((:otherUserId < f.user2.id AND f.status = 'REQ_USER1')
                                  OR (:otherUserId > f.user1.id AND f.status = 'REQ_USER2')))
""")
    List<User> findCommonFriends(Long userId, Long otherUserId);
}
