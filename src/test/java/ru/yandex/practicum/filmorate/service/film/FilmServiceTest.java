package ru.yandex.practicum.filmorate.service.film;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.BaseServiceTest;
import ru.yandex.practicum.filmorate.util.SortBy;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FilmServiceTest extends BaseServiceTest<FilmService> {

    private FilmDto basicFilmDto;

    @Autowired
    public FilmServiceTest(FilmService service) {
        super(service);
    }

    @BeforeEach
    void setUp() {
        basicFilmDto = FilmDto.builder()
                .name("Социальная сеть")
                .description("История Марка Цукера")
                .duration(Duration.ofMinutes(120))
                .releaseDate(LocalDate.of(2010, Month.JANUARY, 1))
                .mpa(new MpaDto(1L, null))
                .genres(
                        Set.of(
                                new GenreDto(1L, null),
                                new GenreDto(2L, null)
                        )
                )
                .build();
    }

    @Test
    void addFilm_shouldReturnAddedFilmWithMpaAndGenreNames() {
        FilmDto addedFilmDto = service.addFilm(basicFilmDto);
        assertThat(getFilmFromService(addedFilmDto.getId()))
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("name", "Социальная сеть")
                .hasFieldOrPropertyWithValue("description", "История Марка Цукера")
                .hasFieldOrPropertyWithValue("duration", Duration.ofMinutes(120))
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2010, Month.JANUARY, 1))
                .satisfies(filmDto -> {
                    assertThat(filmDto.getMpa())
                            .hasNoNullFieldsOrProperties()
                            .hasFieldOrPropertyWithValue("id", 1L)
                            .hasFieldOrProperty("name");
                    assertThat(filmDto.getGenres())
                            .hasSize(2)
                            .extracting(genreDto -> {
                                assertThat(genreDto)
                                        .hasNoNullFieldsOrProperties();

                                return genreDto.getId();
                            })
                            .containsExactly(1L, 2L);
                });
    }

    @Test
    void findFilmById_shouldThrowNotFoundException_whenTheseIsNoFilms() {
        assertThatThrownBy(() -> service.findFilmById(10000L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Фильм не найден");
    }

    @Test
    void findFilmsByDirector_shouldReturnInOrderOfYear() {
        FilmDto filmDtoWithDirector = basicFilmDto.toBuilder()
                .name("2020 film")
                .directors(Set.of(
                        new DirectorDto(2L, null)
                ))
                .releaseDate(LocalDate.of(2020, Month.NOVEMBER, 1))
                .build();
        filmDtoWithDirector = service.addFilm(filmDtoWithDirector);

        FilmDto filmDtoWithDirector2 = filmDtoWithDirector.toBuilder()
                .name("2023 film")
                .directors(Set.of(
                        new DirectorDto(2L, null)
                ))
                .releaseDate(LocalDate.of(2023, Month.NOVEMBER, 1))
                .build();

        service.addFilm(filmDtoWithDirector2);
        List<FilmDto> filmsByDirector = service.findFilmsByDirector(2L, "year");

        assertThat(filmsByDirector)
                .hasSize(2)
                .extracting(FilmDto::getReleaseDate)
                .containsExactly(
                        LocalDate.of(2020, Month.NOVEMBER, 1),
                        LocalDate.of(2023, Month.NOVEMBER, 1)
                );
        assertThat(filmsByDirector)
                .hasSize(2)
                .extracting(FilmDto::getName)
                .containsExactly("2020 film", "2023 film");
    }

    @Test
    void findAllFilms() {
        service.addFilm(basicFilmDto);
        FilmDto secondFilm = basicFilmDto.toBuilder()
                .name("second film")
                .build();
        service.addFilm(secondFilm);
        FilmDto thirdFilm = basicFilmDto.toBuilder()
                .name("third film")
                .build();
        service.addFilm(thirdFilm);

        List<FilmDto> films = service.findAllFilms();

        assertThat(films)
                .isNotNull()
                .hasSize(3)
                .doesNotHaveDuplicates()
                .extracting(FilmDto::getId)
                .containsExactly(1L, 2L, 3L);
        assertThat(films)
                .extracting(FilmDto::getName)
                .containsExactly("Социальная сеть", "second film", "third film");
        assertThat(films)
                .extracting(filmDto -> filmDto.getMpa().getId())
                .containsExactly(1L, 1L, 1L);
    }

    @Test
    void updateFilm() {
        basicFilmDto = service.addFilm(basicFilmDto);
        FilmDto filmDtoUpdatedDirectors = basicFilmDto.toBuilder()
                .directors(Set.of(
                        new DirectorDto(2L, null),
                        new DirectorDto(3L, null)
                ))
                .build();
        filmDtoUpdatedDirectors = service.updateFilm(filmDtoUpdatedDirectors);
        FilmDto filmDtoUpdatedDirectorsRetrieved = service.findFilmById(filmDtoUpdatedDirectors.getId());
        assertThat(filmDtoUpdatedDirectorsRetrieved)
                .hasNoNullFieldsOrProperties()
                .satisfies(filmDto -> {
                    assertThat(filmDto.getDirectors())
                            .extracting(DirectorDto::getId)
                            .containsExactly(2L, 3L);
                    assertThat(filmDto.getDirectors())
                            .extracting(DirectorDto::getName)
                            .containsExactly("Квентин Тарантино", "Вуди Аллен");
                });
    }

    private FilmDto getFilmFromService(Long filmId) {
        return service.findFilmById(filmId);
    }
}