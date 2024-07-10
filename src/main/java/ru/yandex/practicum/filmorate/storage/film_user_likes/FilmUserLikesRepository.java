package ru.yandex.practicum.filmorate.storage.film_user_likes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.filmorate.model.FilmUserLikes;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FilmUserLikesRepository extends JpaRepository<FilmUserLikes, Long> {
    @Query("""
                    SELECT f.id,
                               count(ful.filmUserLikesId) as likes
                        from Film f
                        left join FilmUserLikes ful on f.id = ful.filmUserLikesId
                        group by f.id
                        order by likes desc
                        limit :count
            """)
    List<Long> findPopularFilmsIds(int count);

    void deleteFilmUserLikesByUser(User user);
}
