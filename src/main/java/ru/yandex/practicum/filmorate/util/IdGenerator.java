package ru.yandex.practicum.filmorate.util;

import lombok.experimental.UtilityClass;

import java.util.Iterator;
import java.util.Set;

@UtilityClass
public final class IdGenerator {
    Iterator<Long> usersIterator;
    Iterator<Long> filmsIterator;
    Long maxIdUsers = 0L;
    Long maxIdFilms = 0L;

    public static Long getMaxIdOfUsers(Set<Long> set) {
        return ++maxIdUsers;
    }

    public static Long getMaxIdOfFilms(Set<Long> set) {
        return ++maxIdFilms;
    }
}
