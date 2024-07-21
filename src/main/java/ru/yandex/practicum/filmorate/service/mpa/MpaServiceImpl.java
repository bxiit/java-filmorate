package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {
    private final MpaStorage mpaStorage;

    @Override
    public Mpa findMpaById(long mpaId) {
        return mpaStorage.findMpaById(mpaId)
                .orElseThrow(() -> new NotFoundException("Рейтинг не найден"));
    }

    @Override
    public List<Mpa> findAllMpa() {
        return mpaStorage.findAllMpa();
    }

    @Override
    public boolean isExist(long mpaId) {
        return mpaStorage.findMpaById(mpaId).isPresent();
    }
}
