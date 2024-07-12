package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.FilmorateApplicationTests;

@RequiredArgsConstructor
public abstract class BaseDBStorageTest<T> extends FilmorateApplicationTests {
    protected final T storage;
}
