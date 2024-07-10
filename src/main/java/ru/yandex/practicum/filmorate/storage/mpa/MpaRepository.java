package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.filmorate.model.Mpa;

public interface MpaRepository extends JpaRepository<Mpa, Long> {
}
