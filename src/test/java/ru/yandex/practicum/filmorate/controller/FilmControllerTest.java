package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import ru.yandex.practicum.filmorate.exception.response.ExceptionResponse;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    static ObjectMapper objectMapper = new ObjectMapper();
    private static final String FILMS_ENDPOINT = "http://localhost:8080/films";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_DATE;

    @BeforeAll
    static void setUpAll() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    private HttpResponse<String> createFilmForTest(String name, String description, LocalDate releaseDate, Duration duration) throws IOException, URISyntaxException, InterruptedException {
        String requestBody = "{\"name\": \"%s\", \"description\" : \"%s\", \"releaseDate\": \"%s\", \"duration\" : %d}".formatted(name, description, releaseDate.format(DATE_TIME_FORMATTER), duration.toMinutes());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(FILMS_ENDPOINT))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();

        HttpResponse<String> response;
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertNotNull(response, "The response for creating film is null");
        return response;
    }

    @Test
    void addFilm_shouldReturn404_descriptionIsMoreThan200Letters() throws IOException, URISyntaxException, InterruptedException {
        HttpResponse<String> response = createFilmForTest("Robinzone Cruzo", "«Робинзо́н Кру́зо» — роман английского писателя Даниэля Дефо, впервые опубликованный в апреле 1719 года.«Робинзо́н Кру́зо» — роман английского писателя Даниэля Дефо, впервые опубликованный в апреле 1719 года.«Робинзо́н Кру́зо» — роман английского писателя Даниэля Дефо, впервые опубликованный в апреле 1719 года.",
                LocalDate.of(2010, Month.OCTOBER, 28),
                Duration.ofMinutes(120));

        objectMapper.readValue(response.body(), ExceptionResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode(),
                "Add film returned %d instead of 404".formatted(response.statusCode()));

        ExceptionResponse exceptionResponse = objectMapper.readValue(response.body(), ExceptionResponse.class);
        assertNotNull(exceptionResponse);
        assertEquals("Описание фильма не должно превышать 200 символов", exceptionResponse.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getHttpStatus());
    }

    @Test
    void addFilm_shouldReturn404_releaseDateIsTooOld() throws IOException, URISyntaxException, InterruptedException {
        HttpResponse<String> response = createFilmForTest("Robinzone Cruzo", "«Робинзо́н Кру́зо» — роман английского писателя Даниэля Дефо, впервые опубликованный в апреле 1719 года.",
                LocalDate.of(1895, Month.DECEMBER, 27),
                Duration.ofMinutes(120));

        objectMapper.readValue(response.body(), ExceptionResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.statusCode(),
                "Add film returned %d instead of 404".formatted(response.statusCode()));

        ExceptionResponse exceptionResponse = objectMapper.readValue(response.body(), ExceptionResponse.class);
        assertNotNull(exceptionResponse);
        assertEquals("Дата релиза фильма слишком старая", exceptionResponse.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exceptionResponse.getHttpStatus());
    }

    @Test
    void addFilm_shouldReturn200AndDataOfJustCreatedFilm() throws IOException, URISyntaxException, InterruptedException {
        HttpResponse<String> response = createFilmForTest("Robinzone Cruzo", "«Робинзо́н Кру́зо» — роман английского писателя Даниэля Дефо, впервые опубликованный в апреле 1719 года.",
                LocalDate.of(2010, Month.OCTOBER, 28),
                Duration.ofMinutes(120));

        assertEquals(HttpStatus.CREATED.value(), response.statusCode(),
                "Add film returned %d instead of 200".formatted(response.statusCode()));

        Film filmFromResponse = objectMapper.readValue(response.body(), Film.class);
        assertNotNull(filmFromResponse.getId());
        assertEquals("Robinzone Cruzo", filmFromResponse.getName());
        assertEquals(Duration.ofMinutes(120), filmFromResponse.getDuration());
        assertEquals("«Робинзо́н Кру́зо» — роман английского писателя Даниэля Дефо, впервые опубликованный в апреле 1719 года.", filmFromResponse.getDescription());
        assertEquals(LocalDate.of(2010, Month.OCTOBER, 28), filmFromResponse.getReleaseDate());
    }

    @Test
    void getFilms() throws IOException, URISyntaxException, InterruptedException {
        HttpResponse<String> film1Response = createFilmForTest("Robinzone Cruzo", "«Робинзо́н Кру́зо» — роман английского писателя Даниэля Дефо, впервые опубликованный в апреле 1719 года.",
                LocalDate.of(2010, Month.OCTOBER, 28),
                Duration.ofMinutes(120));
        HttpResponse<String> film2Response = createFilmForTest("Социальная сеть", "Топ топ топ",
                LocalDate.of(2004, Month.NOVEMBER, 16),
                Duration.ofMinutes(120));
        HttpResponse<String> film3Response = createFilmForTest("Мгла", "Маленький городок накрывает сверхъестественный туман, отрезая людей от внешнего мира.",
                LocalDate.of(2007, Month.NOVEMBER, 21),
                Duration.ofMinutes(120).plusMinutes(6));

        Film film1 = objectMapper.readValue(film1Response.body(), Film.class);
        Film film2 = objectMapper.readValue(film2Response.body(), Film.class);
        Film film3 = objectMapper.readValue(film3Response.body(), Film.class);

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .uri(new URI(FILMS_ENDPOINT))
                .build();

        HttpResponse<String> response;
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertNotNull(response, "Response of add film is null");

        List<Film> filmsList = objectMapper.readValue(response.body(), new TypeReference<>() {
        });

        assertNotNull(filmsList);

        assertEquals(HttpStatus.OK.value(), response.statusCode());

        assertTrue(filmsList.stream()
                .anyMatch(f -> f.getId().equals(film1.getId())));

        assertTrue(filmsList.stream()
                .anyMatch(f -> f.getId().equals(film2.getId())));

        assertTrue(filmsList.stream()
                .anyMatch(f -> f.getId().equals(film3.getId())));
    }

    @Test
    void updateFilm() throws IOException, URISyntaxException, InterruptedException {
        HttpResponse<String> film1Response = createFilmForTest("Robinzone Cruzo", "«Робинзо́н Кру́зо» — роман английского писателя Даниэля Дефо, впервые опубликованный в апреле 1719 года.",
                LocalDate.of(2010, Month.OCTOBER, 28),
                Duration.ofMinutes(120));

        Film film1FromResponse = objectMapper.readValue(film1Response.body(), Film.class);

        Film film2 = film1FromResponse.toBuilder()
                .name("Robinzone Cruzo 2")
                .build();

        String film2Json = objectMapper.writeValueAsString(film2);

        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(film2Json))
                .setHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .uri(new URI(FILMS_ENDPOINT))
                .build();

        HttpResponse<String> response;
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        }

        assertNotNull(response, "Response of add film is null");

        Film film2FromResponse = objectMapper.readValue(response.body(), Film.class);
        assertEquals(film1FromResponse.getId(), film2FromResponse.getId());
        assertEquals(film1FromResponse.getReleaseDate(), film2FromResponse.getReleaseDate());
        assertEquals(film1FromResponse.getDescription(), film2FromResponse.getDescription());
        assertEquals(film1FromResponse.getDuration(), film2FromResponse.getDuration());

        assertEquals("Robinzone Cruzo 2", film2FromResponse.getName());
    }
}