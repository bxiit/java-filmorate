package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDBStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreDBStorage genreStorage;

    public Genre findGenreById(long genreId) {
        return genreStorage.findGenreById(genreId)
                .orElseThrow(() -> new NotFoundException("Жанр не найден"));
    }

    public List<Genre> findAllGenres() {
        return this.genreStorage.findAllGenres();
    }

    public boolean isExist(long genreId) {
        return genreStorage.findGenreById(genreId).isPresent();
    }
}
