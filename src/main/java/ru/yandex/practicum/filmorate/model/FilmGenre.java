package ru.yandex.practicum.filmorate.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

//@Entity
//@Table(name = "FILM_GENRE")
public class FilmGenre {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private Long filmGenreId;
//
//    @ManyToOne
//    @JoinColumn(
//            name = "film_id", nullable = false,
//            referencedColumnName = "id",
//            foreignKey = @ForeignKey(name = "fk_film_genre_film")
//    )
//    private Film film;
//
//    @ManyToOne
//    @JoinColumn(
//            name = "genre_id", nullable = false,
//            referencedColumnName = "id",
//            foreignKey = @ForeignKey(name = "fk_film_genre_genre")
//    )
//    private Genre genre;
}
