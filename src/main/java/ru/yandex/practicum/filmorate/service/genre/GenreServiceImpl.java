package ru.yandex.practicum.filmorate.service.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreStorage genreStorage;

    @Override
    public Genre findGenreById(long genreId) {
        return genreStorage.findGenreById(genreId)
                .orElseThrow(() -> new NotFoundException("Жанр не найден"));
    }

    @Override
    public List<Genre> findAllGenres() {
        return this.genreStorage.findAllGenres();
    }

    @Override
    public boolean isExist(long genreId) {
        return genreStorage.findGenreById(genreId).isPresent();
    }
}
