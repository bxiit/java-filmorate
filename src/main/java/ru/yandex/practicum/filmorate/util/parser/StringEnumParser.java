package ru.yandex.practicum.filmorate.util.parser;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class StringEnumParser {
    public static <T extends Enum<T>> Set<T> parseStringToEnum(String string, String separator, Class<T> enumClass) {
        String[] strings = string.split(separator);
        try {
            return Arrays.stream(strings)
                    .map(String::toUpperCase)
                    .map(s -> Enum.valueOf(enumClass, s))
                    .collect(Collectors.toSet());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Не валидный аргумент поиска");
        }
    }
}
