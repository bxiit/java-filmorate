package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.FilmorateApplicationTests;


@RequiredArgsConstructor
public abstract class BaseControllerTest<T> extends FilmorateApplicationTests {
    protected final T controller;
}
