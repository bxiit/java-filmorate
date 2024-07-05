package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;

import java.sql.SQLException;
import java.util.Objects;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor()
@ActiveProfiles("test")
public abstract class BaseControllerTests<T> {
    protected final T controller;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @SneakyThrows({SQLException.class, NullPointerException.class})
    @BeforeEach
    @AfterEach
    void clearStorage() {
        ClassPathResource schemaResource = new ClassPathResource("schema.sql");
        ClassPathResource dataResource = new ClassPathResource("data.sql");
        Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();

        ScriptUtils.executeSqlScript(
                jdbcTemplate.getDataSource().getConnection(),
                schemaResource);
        ScriptUtils.executeSqlScript(jdbcTemplate.getDataSource().getConnection(),
                dataResource);
    }
}
