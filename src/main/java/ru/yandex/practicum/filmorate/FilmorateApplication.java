package ru.yandex.practicum.filmorate;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreRepository;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRepository;

@SpringBootApplication
public class FilmorateApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilmorateApplication.class, args);
    }

    @Bean
    @Transactional
    CommandLineRunner commandLineRunner(MpaRepository mpaRepository, GenreRepository genreRepository) {
        return args -> {
            mpaRepository.deleteAll();
            genreRepository.deleteAll();
            Mpa gMpa = new Mpa();
            gMpa.setName("G");
            mpaRepository.save(gMpa);
            Mpa pgMpa = new Mpa();
            pgMpa.setName("PG");
            mpaRepository.save(pgMpa);
            Mpa pg13Mpa = new Mpa();
            pg13Mpa.setName("PG-13");
            mpaRepository.save(pg13Mpa);

            Genre dramaGenre = new Genre();
            dramaGenre.setName("Драма");
            genreRepository.save(dramaGenre);
            Genre comedyGenre = new Genre();
            comedyGenre.setName("Комедия");
            genreRepository.save(comedyGenre);
            Genre thrillerGenre = new Genre();
            thrillerGenre.setName("Триллер");
            genreRepository.save(thrillerGenre);
            Genre actionGenre = new Genre();
            actionGenre.setName("Боевик");
            genreRepository.save(actionGenre);
        };
    }
}
