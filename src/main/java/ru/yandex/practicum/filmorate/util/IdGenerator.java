package ru.yandex.practicum.filmorate.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class IdGenerator {
    Long maxIdUsers = 0L;
    Long maxIdFilms = 0L;

    public static Long getMaxIdOfUsers() {
        return ++maxIdUsers;
    }

    public static Long getMaxIdOfFilms() {
        return ++maxIdFilms;
    }
}
