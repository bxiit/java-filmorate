package ru.yandex.practicum.filmorate.util.parser;

import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class StringEnumParser {
    public static <T extends Enum<T>> Set<T> parseStringToEnum(String string, String separator, Class<T> enumClass) {
        String[] strings = string.split(separator);
        Set<T> collect = Arrays.stream(strings)
                .map(String::toUpperCase)
                .map(s -> Enum.valueOf(enumClass, s))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        System.out.println(string);
        System.out.println(collect);
        return collect;
    }
}
