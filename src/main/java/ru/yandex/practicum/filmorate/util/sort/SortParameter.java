package ru.yandex.practicum.filmorate.util.sort;

import java.util.Comparator;

public interface SortParameter<T> {
    Comparator<T> sortComparator();
}