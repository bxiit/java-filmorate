package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import ru.yandex.practicum.filmorate.model.User;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    static ObjectMapper objectMapper = new ObjectMapper();
    private static final String USERS_ENDPOINT = "http://localhost:8080/users";

    @BeforeAll
    static void setUpAll() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private User createUserForTest(String login, String name, String email, LocalDate birthday) throws IOException, URISyntaxException, InterruptedException {
        User user = User.builder()
                .login(login)
                .name(name)
                .email(email)
                .birthday(birthday)
                .build();

        String userJson = objectMapper.writeValueAsString(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(USERS_ENDPOINT))
                .POST(HttpRequest.BodyPublishers.ofString(userJson))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        HttpResponse<String> response;
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }

        String responseBody = response.body();

        User userFromResponse = objectMapper.readValue(responseBody, User.class);

        assertNotNull(response, "The response is null");
        assertEquals(200, response.statusCode(),
                "User controller returns %d instead of 200".formatted(response.statusCode()));
        assertEquals(login, userFromResponse.getLogin());
        assertEquals(name == null ? login : name, userFromResponse.getName());
        assertEquals(email, userFromResponse.getEmail());
        assertEquals(birthday, userFromResponse.getBirthday());
        return userFromResponse;
    }

    @Test
    void createUser_shouldReturnStatusCode200AndDataOfJustCreatedUser() throws URISyntaxException, IOException, InterruptedException {
        User userFromResponse = createUserForTest("nickname", "Bexeiit", "bexeiitatabek@yandex.kz",
                LocalDate.of(2004, Month.NOVEMBER, 16));

        assertEquals("nickname", userFromResponse.getLogin());
        assertEquals("Bexeiit", userFromResponse.getName());
        assertEquals("bexeiitatabek@yandex.kz", userFromResponse.getEmail());
        assertEquals(LocalDate.of(2004, Month.NOVEMBER, 16), userFromResponse.getBirthday());
    }

    @Test
    void createUser_shouldReturnStatusCode200AndDataOfJustCreatedUser_loginAndNameAreEqual() throws URISyntaxException, IOException, InterruptedException {
        User userFromResponse = createUserForTest("nickname", null, "bexeiitatabek@yandex.kz",
                LocalDate.of(2004, Month.NOVEMBER, 16));

        assertEquals("nickname", userFromResponse.getLogin());
        assertEquals("nickname", userFromResponse.getLogin());
        assertEquals("bexeiitatabek@yandex.kz", userFromResponse.getEmail());
        assertEquals(LocalDate.of(2004, Month.NOVEMBER, 16), userFromResponse.getBirthday());
    }

    @Test
    void updateUser_shouldReturn500BecauseOfValidation_loginContainsSpace() throws URISyntaxException, IOException, InterruptedException {
        User userFromCreateResponse = createUserForTest("nickname", "Bexeiit", "bexeiitatabek@yandex.kz",
                LocalDate.of(2004, Month.NOVEMBER, 16));

        User userUpdate = userFromCreateResponse.toBuilder()
                .login("nick name")
                .build();

        String userJson = objectMapper.writeValueAsString(userUpdate);

        HttpRequest updateRequest = HttpRequest.newBuilder()
                .uri(new URI(USERS_ENDPOINT))
                .PUT(HttpRequest.BodyPublishers.ofString(userJson))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        HttpResponse<String> updateResponse;
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            updateResponse = httpClient.send(updateRequest, HttpResponse.BodyHandlers.ofString());
        }
        assertEquals(HttpStatus.BAD_REQUEST.value(), updateResponse.statusCode());
    }

    @Test
    void updateUser_shouldReturn200AndDataOfJustUpdatedUser() throws URISyntaxException, IOException, InterruptedException {
        User user1FromResponse = createUserForTest("nickname", "Bexeiit", "bexeiitatabek@yandex.kz",
                LocalDate.of(2004, Month.NOVEMBER, 16));

        User user2 = user1FromResponse.toBuilder()
                .login("nickname2")
                .build();

        String user2Json = objectMapper.writeValueAsString(user2);

        HttpRequest requestOfUser2 = HttpRequest.newBuilder()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .uri(new URI(USERS_ENDPOINT))
                .PUT(HttpRequest.BodyPublishers.ofString(user2Json))
                .build();

        HttpResponse<String> responseOfUser2;
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            responseOfUser2 = httpClient.send(requestOfUser2, HttpResponse.BodyHandlers.ofString());
        }

        assertNotNull(responseOfUser2, "Update response is null");
        User user2FromResponse = objectMapper.readValue(responseOfUser2.body(), User.class);

        assertEquals(user1FromResponse.getId(), user2FromResponse.getId());
        assertEquals("nickname2", user2FromResponse.getLogin(), "Login did not updated");
        assertEquals(user1FromResponse.getEmail(), user2FromResponse.getEmail(), "Email should have not been changed");
        assertEquals(user1FromResponse.getName(), user2FromResponse.getName(), "Name should have not been changed");
        assertEquals(user1FromResponse.getBirthday(), user2FromResponse.getBirthday(), "Birthday should have not been changed");
    }

    @Test
    void getAllUsers() throws IOException, URISyntaxException, InterruptedException {
        User user1 = createUserForTest("nickname", "Bexeiit", "bexeiitatabek@yandex.kz",
                LocalDate.of(2004, Month.NOVEMBER, 16));
        User user2 = createUserForTest("frenchfry", "Aiym", "shadena@gmail.com",
                LocalDate.of(2004, Month.NOVEMBER, 16));
        User user3 = createUserForTest("sergek", "Sergekbek", "sergekbek@mail.ru",
                LocalDate.of(2004, Month.NOVEMBER, 16));

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI("http://localhost:8080/users"))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        HttpResponse<String> response;
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertNotNull(response);

        List<User> usersList = objectMapper.readValue(response.body(), new TypeReference<>() {});

        assertEquals(HttpStatus.OK.value(), response.statusCode());

        assertTrue(usersList.stream()
                .anyMatch(u -> u.getId().equals(user1.getId())));

        assertTrue(usersList.stream()
                .anyMatch(u -> u.getId().equals(user2.getId())));

        assertTrue(usersList.stream()
                .anyMatch(u -> u.getId().equals(user3.getId())));
    }
}