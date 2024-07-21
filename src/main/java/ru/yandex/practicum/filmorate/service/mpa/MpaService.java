package ru.yandex.practicum.filmorate.service.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaService {
    Mpa findMpaById(long mpaId);

    List<Mpa> findAllMpa();

    boolean isExist(long mpaId);
}
