package ru.yandex.practicum.filmorate.util.enums.sort;

import java.util.Comparator;

public interface SortParameter<T> {
    Comparator<T> sortComparator();
}