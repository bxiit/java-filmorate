package ru.yandex.practicum.filmorate.cache;

public interface MessagePublisher {
    void publish(final String message);
}