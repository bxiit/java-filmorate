package ru.yandex.practicum.filmorate.util;

public enum SortBy {
    YEAR,
    LIKES;

    public static SortBy from(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "year" -> YEAR;
            case "likes", "like" -> LIKES;
            default -> null;
        };
    }
}
