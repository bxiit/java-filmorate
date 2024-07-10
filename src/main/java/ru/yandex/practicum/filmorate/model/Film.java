package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.util.DurationDeserializer;
import ru.yandex.practicum.filmorate.util.DurationSerializer;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

/**
 * Film.
 */
@Data
@EqualsAndHashCode(of = {"id"})
@AllArgsConstructor
@NoArgsConstructor
@Entity
//@Table(name = "FILM")
public class Film {
//
//    public Film() {
////        this.likedUsersIDs = new HashSet<>();
////        this.genres = new HashSet<>();
//    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String name;

    @Column(columnDefinition = "VARCHAR", length = 200)
    private String description;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(columnDefinition = "DATE")
    private LocalDate releaseDate;

    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    @Column(columnDefinition = "BIGINT")
    private Duration duration;

//    @OneToMany()
//    private Set<FilmUserLikes> likedUsersIDs;

    @ManyToOne
    @JoinColumn(name = "MPA_ID", columnDefinition = "BIGINT",
            foreignKey = @ForeignKey(name = "fk_mpa_id")
    )
    private Mpa mpa;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinTable(
            name = "film_genre_hiber",
            joinColumns = {
                    @JoinColumn(name = "film_id", foreignKey = @ForeignKey(name = "fk_film_genre_hiber_film_id"))
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "genre_id", foreignKey = @ForeignKey(name = "fk_film_genre_hiber_genre_id"))
            }
    )
    private Set<Genre> genres;

    @OneToMany(
            fetch = FetchType.EAGER,
            mappedBy = "film"
    )
    private Set<FilmUserLikes> likes;
}
