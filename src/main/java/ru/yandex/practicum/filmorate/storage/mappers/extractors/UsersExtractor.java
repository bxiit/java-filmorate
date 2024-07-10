package ru.yandex.practicum.filmorate.storage.mappers.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class UsersExtractor implements ResultSetExtractor<List<User>> {
    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Long, User> users = new HashMap<>();

        while (rs.next()) {
            long userId = rs.getLong("user_id");
            if (users.containsKey(userId)) {
                User user = users.get(userId);
//                user.getFriends().add(rs.getLong("friend_id"));
                continue;
            }
            boolean hasFriendIdColumn = hasFriendIdColumn(rs);
            HashSet<Long> friendsIdsSet = new HashSet<>();
            if (hasFriendIdColumn && rs.getLong("friend_id") > 0) {
                friendsIdsSet.add(rs.getLong("friend_id"));
            }
            User user = User.builder()
                    .id(userId)
                    .name(rs.getString("name"))
                    .login(rs.getString("login"))
                    .email(rs.getString("email"))
                    .birthday(rs.getDate("birthday").toLocalDate())
//                    .friends(friendsIdsSet)
                    .build();
            users.put(userId, user);
        }

        return new ArrayList<>(users.values());
    }

    private boolean hasFriendIdColumn(ResultSet rs) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                if (rs.getObject(i).equals("friend_id")) {
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
