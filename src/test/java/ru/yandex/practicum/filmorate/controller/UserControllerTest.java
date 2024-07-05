package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class UserControllerTest extends BaseControllerTests<UserController> {

    static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public UserControllerTest(UserController controller) {
        super(controller);
    }

    @BeforeAll
    static void setUp() {
        objectMapper.registerModules(new JavaTimeModule());
    }

    @Test
    void testUserModel_shouldThrowNullPointerException_releaseDateIsNull() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Bexeiit");
        request.setBirthday(null);
        request.setEmail("bexeiitatabek@yandex.kz");
        request.setLogin("nickname");

        Validator validator;
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }

        assertNotNull(validator);

        Set<ConstraintViolation<NewUserRequest>> validatedUserSet = validator.validate(request);
        assertEquals(1, validatedUserSet.size());
        assertTrue(validatedUserSet.stream()
                .anyMatch(results -> results.getMessage().equals("Пустая дата рождения")));

        // при валидации null.isAfter() бросить NPE
        assertThrows(NullPointerException.class, () -> controller.addUser(request));
    }

    @Test
    void testUserModel_shouldReturnMessageAboutInvalidEmail_invalidEmail() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Bexeiit");
        request.setBirthday(LocalDate.of(2004, Month.NOVEMBER, 16));
        request.setEmail("@@bexeiitatabekyandex.kz");
        request.setLogin("nickname");

        Validator validator;
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }

        assertNotNull(validator);

        Set<ConstraintViolation<NewUserRequest>> validatedUserSet = validator.validate(request);
        assertEquals(1, validatedUserSet.size());
        assertTrue(validatedUserSet.stream()
                .anyMatch(results -> results.getMessage().equals("Неверный формат электронной почты")));
    }

    @Test
    void testUserModel_shouldNotReturnAnything() {
        NewUserRequest request = new NewUserRequest();
        request.setName("Bexeiit");
        request.setBirthday(LocalDate.of(2004, Month.NOVEMBER, 16));
        request.setEmail("bexeiitatabek@yandex.kz");
        request.setLogin("nickname");

        Validator validator;
        try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
            validator = validatorFactory.getValidator();
        }

        assertNotNull(validator);

        Set<ConstraintViolation<NewUserRequest>> validatedUserSet = validator.validate(request);
        assertEquals(0, validatedUserSet.size());

        UserDto userResponseEntity = controller.addUser(request);
        assertNotNull(userResponseEntity);
        assertNotNull(userResponseEntity.getId());
        assertTrue(userResponseEntity.getId() > 0);
        assertEquals("Bexeiit", userResponseEntity.getName());
        assertEquals("nickname", userResponseEntity.getLogin());
        assertEquals("bexeiitatabek@yandex.kz", userResponseEntity.getEmail());
        assertEquals(LocalDate.of(2004, Month.NOVEMBER, 16), userResponseEntity.getBirthday());
    }

    @Test
    void testUsersIds_shouldReturnSequencedIds() {
        NewUserRequest request = new NewUserRequest();
        request.setName("testId name");
        request.setLogin("testId-login");
        request.setBirthday(LocalDate.now());
        request.setEmail("testId@gmail.com");

        UserDto userDto = controller.addUser(request);
        assertNotNull(userDto);
        assertNotNull(userDto.getId());
        assertTrue(userDto.getId() > 0);

        NewUserRequest request1 = new NewUserRequest();
        request1.setName("testId name");
        request1.setLogin("testId-login1");
        request1.setBirthday(LocalDate.now());
        request1.setEmail("testId2@gmail.com");
        UserDto userDto1 = controller.addUser(request1);
        assertNotNull(userDto1);
        assertNotNull(userDto1.getId());
        assertTrue(userDto1.getId() > 0);

        assertTrue(userDto.getId() < userDto1.getId());
        assertEquals(userDto.getId() + 1, userDto1.getId());
    }
}