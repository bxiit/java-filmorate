package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserControllerTest {

    static UserController userController;
    static ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setUp() {
        UserService userService = new UserService(new InMemoryUserStorage());
        userController = new UserController(userService);
        objectMapper.registerModules(new JavaTimeModule());
    }

    @Test
    void testUserModel_shouldThrowNullPointerException_releaseDateIsNull() {
        User user = User.builder()
                .name("Bexeiit")
                .email("bexeiitatabek@yandex.kz")
                .birthday(null)
                .login("nickname")
                .build();

        Validator validator;
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }

        assertNotNull(validator);

        Set<ConstraintViolation<User>> validatedUserSet = validator.validate(user);
        assertEquals(1, validatedUserSet.size());
        assertTrue(validatedUserSet.stream()
                .anyMatch(results -> results.getMessage().equals("Пустая дата рождения")));

        // при валидации null.isAfter() бросить NPE
        assertThrows(NullPointerException.class, () -> userController.addUser(user));
    }

    @Test
    void testUserModel_shouldReturnMessageAboutInvalidEmail_invalidEmail() {
        User user = User.builder()
                .name("Bexeiit")
                .email("@@@bexeiitatabekyandex.kz")
                .birthday(LocalDate.of(2004, Month.NOVEMBER, 16))
                .login("nickname")
                .build();

        Validator validator;
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }

        assertNotNull(validator);

        Set<ConstraintViolation<User>> validatedUserSet = validator.validate(user);
        assertEquals(1, validatedUserSet.size());
        assertTrue(validatedUserSet.stream()
                .anyMatch(results -> results.getMessage().equals("Неверный формат электронной почты")));
    }

    @Test
    void testUserModel_shouldNotReturnAnything() {
        User user = User.builder()
                .name("Bexeiit")
                .login("nickname")
                .email("bexeiitatabek@yandex.kz")
                .birthday(LocalDate.of(2004, Month.NOVEMBER, 16))
                .build();

        Validator validator;
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }

        assertNotNull(validator);

        Set<ConstraintViolation<User>> validatedUserSet = validator.validate(user);
        assertEquals(0, validatedUserSet.size());

        ResponseEntity<User> userResponseEntity = userController.addUser(user);
        User userResponseEntityBody = userResponseEntity.getBody();
        assertNotNull(userResponseEntityBody);
        assertNotNull(userResponseEntityBody.getId());
        assertTrue(userResponseEntityBody.getId() > 0);
        assertEquals("Bexeiit", userResponseEntityBody.getName());
        assertEquals("nickname", userResponseEntityBody.getLogin());
        assertEquals("bexeiitatabek@yandex.kz", userResponseEntityBody.getEmail());
        assertEquals(LocalDate.of(2004, Month.NOVEMBER, 16), userResponseEntityBody.getBirthday());
    }

    @Test
    void testUsersIds_shouldReturnSequencedIds() {
        User user = User.builder()
                .name("testId name")
                .login("testId-login")
                .email("testId@gmail.com")
                .birthday(LocalDate.now())
                .build();

        ResponseEntity<User> userResponseEntity = userController.addUser(user);
        User userFromBody = userResponseEntity.getBody();
        assertNotNull(userFromBody);
        assertNotNull(userFromBody.getId());
        assertTrue(userFromBody.getId() > 0);

        User user1 = user.toBuilder()
                .email("testId2@gmail.com")
                .build();
        ResponseEntity<User> userResponseEntity1 = userController.addUser(user1);
        User user1FromBody = userResponseEntity1.getBody();
        assertNotNull(user1FromBody);
        assertNotNull(user1FromBody.getId());
        assertTrue(user1FromBody.getId() > 0);

        assertTrue(userFromBody.getId() < user1FromBody.getId());
        assertEquals(userFromBody.getId() + 1, user1FromBody.getId());
    }
}