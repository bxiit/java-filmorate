package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.filmorate.model.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {
}
