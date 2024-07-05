package ru.yandex.practicum.filmorate.storage.mappers.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
// todo: implement
public class UsersExtractor implements ResultSetExtractor<List<User>> {
    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
//        Map<Integer, User> users = new HashMap<>();
//
//        //итерируемся по всем строкам ResultSet
//        while (rs.next()) {
//            int userId = rs.getInt("user_id");
//            // проверяем, есть ли пользователь в userIdToUser
//            // если нет - создаем, если есть то достаем его из мапы и в его список друзей добавляем int friendId = rs.getInt("friend_id");
//            //...
//        }
//
//        // Преобразуем Map в список
//        return new ArrayList<>(users.values());
        return List.of();
    }
}
