package ru.yandex.practicum.filmorate.util.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {
    public static final String SQL_WILDCARD = "%";

    public static String addWildCards(String string) {
        return SQL_WILDCARD + string + SQL_WILDCARD;
    }
}
