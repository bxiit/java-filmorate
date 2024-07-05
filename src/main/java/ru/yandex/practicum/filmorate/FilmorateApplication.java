package ru.yandex.practicum.filmorate;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {
    @SneakyThrows
    public static void main(String[] args) {

//        try (Connection connection = DriverManager.getConnection("jdbc:h2:tcp://localhost/C:\\Users\\aruzhan one love\\Desktop\\zhava\\sprint_10\\java-filmorate\\db",
//                "sa", "password");) {
//            ResultSet resultSet;
//            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM USERS")) {
//                resultSet = statement.executeQuery();
//            }
//            while (resultSet.next()) {
//				System.out.println(resultSet.getLong("user_id"));
//			}
//		}
        SpringApplication.run(FilmorateApplication.class, args);
    }

}
