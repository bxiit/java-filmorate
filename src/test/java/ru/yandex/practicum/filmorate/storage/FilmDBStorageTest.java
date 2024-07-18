package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.mappers.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDBStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDBStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

public class FilmDBStorageTest extends BaseDBStorageTest<FilmDBStorage> {


    @Autowired
    private UserDBStorage userDBStorage;

    @Autowired
    public FilmDBStorageTest(FilmDBStorage storage, UserDBStorage userDBStorage) {
        super(storage);
        this.userDBStorage = userDBStorage;
    }

    @Test
    void testAddFilm_shouldBeOptionalWithJustAddedFilm() {
        MpaDto pg13MpaDto = new MpaDto();
        pg13MpaDto.setId(3L);
        Set<GenreDto> genresDtoSet = new TreeSet<>();
        GenreDto dramaGenreDto = new GenreDto();
        dramaGenreDto.setId(2L);
        genresDtoSet.add(dramaGenreDto);
        Film film = Film.builder()
                .name("Социальная сеть")
                .description("Один из лучших фильмов")
                .releaseDate(LocalDate.of(2010, Month.OCTOBER, 28))
                .duration(Duration.ofMinutes(120))
                .mpa(pg13MpaDto)
                .genres(genresDtoSet)
                .build();

        storage.addFilm(film);

        Optional<Film> addedFilm = storage.findFilmById(film.getId());

        assertThat(addedFilm)
                .isPresent()
                .hasValueSatisfying(
                        f -> assertThat(f).hasFieldOrPropertyWithValue("name", "Социальная сеть")
                        .hasFieldOrPropertyWithValue(
                                "description", "Один из лучших фильмов"
                        )
                        .hasFieldOrPropertyWithValue(
                                "releaseDate", LocalDate.of(2010, Month.OCTOBER, 28)
                        )
                        .hasFieldOrPropertyWithValue("duration", Duration.ofMinutes(120))
                );
    }

    @Test
    void testFindFilmById_shouldBeEmptyOptional_whenFindNotExistingFilm() {
        Optional<Film> notExistingFilm = storage.findFilmById(1);
        assertThat(notExistingFilm)
                .isEmpty();
    }

    @Test
    void testFindAllFilms_shouldReturnEmptyList() {
        List<Film> films = storage.findAllFilms();
        assertThat(films).isEmpty();
    }

    @Test
    void testDeleteFilmById_shouldDeleteFilm() {
        MpaDto pg13MpaDto = new MpaDto();
        pg13MpaDto.setId(3L);
        Set<GenreDto> genresDtoSet = new TreeSet<>();
        GenreDto dramaGenreDto = new GenreDto();
        dramaGenreDto.setId(2L);
        genresDtoSet.add(dramaGenreDto);
        Film film = Film.builder()
                .name("Социальная сеть")
                .description("Один из лучших фильмов")
                .releaseDate(LocalDate.of(2010, Month.OCTOBER, 28))
                .duration(Duration.ofMinutes(120))
                .mpa(pg13MpaDto)
                .genres(genresDtoSet)
                .build();

        storage.addFilm(film);
        boolean isDeleted = storage.deleteFilmById(film.getId());
        assertThat(isDeleted).isTrue();
        Optional<Film> deletedFilm = storage.findFilmById(film.getId());
        assertThat(deletedFilm).isEmpty();
    }

    @Test
    void testLikeFilm_shouldAddLikeToFilm() {
        User userToAdd = User.builder()
                .name("Bexeiit")
                .email("atabekbekseiit@gmail.com")
                .login("bxiit")
                .birthday(LocalDate.now())
                .build();
        User addedUser = userDBStorage.addUser(userToAdd);

        MpaDto pg13MpaDto = new MpaDto();
        pg13MpaDto.setId(3L);
        Set<GenreDto> genresDtoSet = new TreeSet<>();
        GenreDto dramaGenreDto = new GenreDto();
        dramaGenreDto.setId(2L);
        genresDtoSet.add(dramaGenreDto);
        Film film = Film.builder()
                .name("Социальная сеть")
                .description("Один из лучших фильмов")
                .releaseDate(LocalDate.of(2010, Month.OCTOBER, 28))
                .duration(Duration.ofMinutes(120))
                .mpa(pg13MpaDto)
                .genres(genresDtoSet)
                .build();

        storage.addFilm(film);
        boolean isLiked = storage.likeFilm(film.getId(), 1L);
        assertThat(isLiked).isTrue();
    }

    @Test
    void testUnlikeFilm_shouldRemoveLikeFromFilm() {
        User userToAdd = User.builder()
                .name("Bexeiit")
                .email("atabekbekseiit@gmail.com")
                .login("bxiit")
                .birthday(LocalDate.now())
                .build();
        User addedUser = userDBStorage.addUser(userToAdd);

        MpaDto pg13MpaDto = new MpaDto();
        pg13MpaDto.setId(3L);
        Set<GenreDto> genresDtoSet = new TreeSet<>();
        GenreDto dramaGenreDto = new GenreDto();
        dramaGenreDto.setId(2L);
        genresDtoSet.add(dramaGenreDto);
        Film film = Film.builder()
                .name("Социальная сеть")
                .description("Один из лучших фильмов")
                .releaseDate(LocalDate.of(2010, Month.OCTOBER, 28))
                .duration(Duration.ofMinutes(120))
                .mpa(pg13MpaDto)
                .genres(genresDtoSet)
                .build();

        storage.addFilm(film);
        storage.likeFilm(film.getId(), 1L);
        boolean isUnliked = storage.unlikeFilm(film.getId(), 1L);
        assertThat(isUnliked).isTrue();
    }

    @Test
    void testUpdateFilm_shouldBeFoundJustUpdatedFilm() {
        MpaDto pg13MpaDto = new MpaDto();
        pg13MpaDto.setId(3L);
        Set<GenreDto> genresDtoSet = new TreeSet<>();
        GenreDto dramaGenreDto = new GenreDto();
        dramaGenreDto.setId(2L);
        genresDtoSet.add(dramaGenreDto);
        Film film = Film.builder()
                .name("Социальная сеть")
                .description("Один из лучших фильмов")
                .releaseDate(LocalDate.of(2010, Month.OCTOBER, 28))
                .duration(Duration.ofMinutes(120))
                .mpa(pg13MpaDto)
                .genres(genresDtoSet)
                .build();

        storage.addFilm(film);

        Optional<Film> addedFilmOptional = storage.findFilmById(film.getId());
        Film addedFilm = addedFilmOptional.get();

        FilmDto updateUserRequest = FilmDto.builder().build();
        updateUserRequest.setName("updated name");

        MpaDto pgMpaDto = new MpaDto();
        pgMpaDto.setId(2L);
        updateUserRequest.setMpa(pgMpaDto);

        GenreDto comedyGenreDto = new GenreDto();
        comedyGenreDto.setId(1L);
        genresDtoSet.add(comedyGenreDto);

        updateUserRequest.setGenres(genresDtoSet);

        Film updatedFilmBeforeSave = FilmMapper.MAPPER.updateFilmFields(addedFilm, updateUserRequest);

        storage.updateFilm(updatedFilmBeforeSave);

        Optional<Film> updatedFilmAfterSave = storage.findFilmById(updatedFilmBeforeSave.getId());

        assertThat(updatedFilmAfterSave)
                .isPresent()
                .hasValueSatisfying(f -> assertThat(f)
                        .hasFieldOrPropertyWithValue("name", "updated name")
                        .hasFieldOrPropertyWithValue("mpa", pgMpaDto)
                        .hasFieldOrPropertyWithValue("genres", genresDtoSet));
    }
}
