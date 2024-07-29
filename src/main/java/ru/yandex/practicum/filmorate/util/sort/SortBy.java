package ru.yandex.practicum.filmorate.util.sort;

import ru.yandex.practicum.filmorate.dto.film.FilmDto;

import java.util.Comparator;

public enum SortBy implements SortParameter<FilmDto> {
    YEAR {
        @Override
        public Comparator<FilmDto> sortComparator() {
            return Comparator.comparing(FilmDto::getReleaseDate);
        }
    },

    LIKES {
        @Override
        public Comparator<FilmDto> sortComparator() {
            return Comparator.comparing((filmDto) -> filmDto.getLikedUsersIDs().size(), Comparator.reverseOrder());
        }
    };
}