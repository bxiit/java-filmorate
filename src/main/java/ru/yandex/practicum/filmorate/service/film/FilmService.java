package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.mappers.MpaMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmUserLikes;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmRepository;
import ru.yandex.practicum.filmorate.storage.film_user_likes.FilmUserLikesRepository;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRepository;
import ru.yandex.practicum.filmorate.storage.user.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    public static final String USER_NOT_FOUND_MESSAGE = "Пользователь не найден";
    public static final String FILM_NOT_FOUND_MESSAGE = "Фильм не найден";

    private final FilmRepository filmRepository;

    private final MpaRepository mpaRepository;

    private final GenreRepository genreRepository;

    private final FilmUserLikesRepository likesRepository;

    private final UserRepository userRepository;

    public FilmDto addFilm(FilmDto request) {
        Film film = FilmMapper.MAPPER.mapNewFilmToFilm(request);
        filmRepository.save(film);
        film = filmRepository.getReferenceById(film.getId());
        FilmDto filmDto = FilmMapper.MAPPER.mapToFilmDto(film);
        setGenreName(filmDto);
        setMpaName(filmDto);
        return filmDto;
    }

    public FilmDto findFilmById(long filmId) {
        return filmRepository.findById(filmId)
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException(FILM_NOT_FOUND_MESSAGE));
    }

    public List<FilmDto> findAllFilms() {
        return filmRepository.findAll().stream()
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .toList();
    }

    public FilmDto updateFilm(FilmDto request) {
        Film updatedFilm = filmRepository.findById(request.getId())
                .map(f -> FilmMapper.MAPPER.updateFilmFields(f, request))
                .orElseThrow(() -> new NotFoundException(FILM_NOT_FOUND_MESSAGE));

        filmRepository.save(updatedFilm);
        FilmDto updatedFilmDto = FilmMapper.MAPPER.mapToFilmDto(updatedFilm);
        setGenreName(updatedFilmDto);
        setMpaName(updatedFilmDto);

        return updatedFilmDto;
    }

    public void deleteFilmById(long filmId) {
        filmRepository.deleteById(filmId);
    }

    // fixme: displays only the most liked one
    public List<FilmDto> findPopularFilmsByCount(int count) {
        return likesRepository.findPopularFilmsIds(count).stream()
                .map(filmRepository::findById)
                .map(Optional::orElseThrow)
                .map(FilmMapper.MAPPER::mapToFilmDto)
                .map(this::setGenreName)
                .map(this::setMpaName)
                .toList();
    }

    public void likeFilm(long filmId, long userId) {
        likeFilmAction(filmId, userId, true);
    }

    public void unlikeFilm(long filmId, long userId) {
        likeFilmAction(filmId, userId, false);
    }

    private void likeFilmAction(long filmId, long userId, boolean isLike) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND_MESSAGE));
        if (isLike) {
            Film film = filmRepository.findById(filmId)
                    .orElseThrow(() -> new NotFoundException(FILM_NOT_FOUND_MESSAGE));
            FilmUserLikes likes = new FilmUserLikes(film, user);
            likesRepository.save(likes);
        } else {
            likesRepository.deleteFilmUserLikesByUser(user);
        }
    }

    private FilmDto setMpaName(FilmDto filmDto) {
        MpaDto mpaDto = mpaRepository.findById(filmDto.getMpa().getId())
                .map(MpaMapper.MPA_MAPPER::mapToDto)
                .orElseThrow(() -> new NotFoundException("Рейтинг не найден"));
        filmDto.setMpa(mpaDto);
        return filmDto;
    }

    private FilmDto setGenreName(FilmDto filmDto) {
        Set<GenreDto> genres = filmDto.getGenres().stream()
                .map(genreDto -> genreRepository.findById(genreDto.getId()))
                .map(genre -> genre.orElseThrow(() -> new NotFoundException("Жанр не найден")))
                .map(GenreMapper.GENRE_MAPPER::mapToDto)
                .collect(Collectors.toSet());
        filmDto.setGenres(genres);
        return filmDto;
    }
}
