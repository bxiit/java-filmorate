package ru.yandex.practicum.filmorate.util;

import lombok.experimental.UtilityClass;

import java.util.Set;

@UtilityClass
public final class IdGenerator {
    public static Long getMaxIdOfMap(Set<Long> set) {
        long maxId = set.stream()
                .mapToLong(longId -> longId)
                .max()
                .orElse(0);
        return ++maxId;
    }
}
