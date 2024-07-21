package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.FilmorateApplicationTests;

@RequiredArgsConstructor
public abstract class BaseServiceTest<T> extends FilmorateApplicationTests {
    protected final T service;
}
