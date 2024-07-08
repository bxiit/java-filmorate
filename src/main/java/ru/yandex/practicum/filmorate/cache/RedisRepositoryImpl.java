package ru.yandex.practicum.filmorate.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Repository
public class RedisRepositoryImpl implements RedisRepository {
    private static final String KEY = "Film";
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, Long, String> hashOperations;
    private final ObjectMapper objectMapper;

    @Autowired
    public RedisRepositoryImpl(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    @SneakyThrows
    @Override
    public void add(final Film film) {
        String filmJson = objectMapper.writeValueAsString(film);
        hashOperations.put(KEY, (long) film.hashCode(), filmJson);
    }

    @SneakyThrows
    @Override
    public Film find(final Long id) {
        String filmJson = hashOperations.get(KEY, id);
        if (filmJson == null) {
            return null;
        }
        return objectMapper.readValue(filmJson, Film.class);
    }

    @Override
    public List<Film> findPopularFilms() {
        return List.of();
    }
}