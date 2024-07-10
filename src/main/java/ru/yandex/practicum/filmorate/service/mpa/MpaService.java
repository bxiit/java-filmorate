package ru.yandex.practicum.filmorate.service.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaRepository mpaRepository;

    public Mpa findMpaById(long mpaId) {
        return mpaRepository.findById(mpaId)
                .orElseThrow(() -> new NotFoundException("Рейтинг не найден"));
    }

    public List<Mpa> findAllMpa() {
        return mpaRepository.findAll();
    }
}
