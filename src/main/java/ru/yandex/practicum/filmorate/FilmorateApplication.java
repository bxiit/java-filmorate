package ru.yandex.practicum.filmorate;

import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FilmorateApplication {
    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(FilmorateApplication.class, args);
    }
}
