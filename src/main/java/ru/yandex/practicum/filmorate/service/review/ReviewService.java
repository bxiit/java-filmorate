package ru.yandex.practicum.filmorate.service.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;


    @Autowired
    public ReviewService(@Qualifier("ReviewDbStorage") ReviewStorage reviewStorage, @Qualifier("UserDBStorage") UserStorage userStorage,
                         @Qualifier("FilmDBStorage") FilmStorage filmStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;

    }

    public Review addReview(Review review) {
        if (review.getUserId() != null && userStorage.findUserById(review.getUserId()).isEmpty()) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (review.getFilmId() != null && filmStorage.findFilmById(review.getFilmId()).isEmpty()) {
            throw new NotFoundException("Фильм не найден");
        }
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        return reviewStorage.updateReview(review);
    }

    public boolean deleteReview(long reviewId) {
        return reviewStorage.deleteReview(reviewId);
    }

    public List<Review> getReviews(long filmId, int count) {
        if (filmId != 0) {
            return reviewStorage.getAllReviewsByFilmId(filmId).stream()
                    .limit(count)
                    .toList();

        } else {
            return reviewStorage.getAllReviews().stream()
                    .limit(count)
                    .toList();
        }
    }

    public Optional<Review> getReviewById(long reviewId) {
        Optional<Review> review = reviewStorage.getReviewById(reviewId);
        if (review.isEmpty()) {
            throw new NotFoundException("Отзыв не найден");
        }
        return review;
    }

    public void likeReview(long reviewId, long userId) {
        Optional<User> user = userStorage.findUserById(userId);
        Optional<Review> review = reviewStorage.getReviewById(reviewId);
        if (!user.isEmpty() && !review.isEmpty()) {
            if (review.get().getDislikedBy().contains(userId)) {
                reviewStorage.addUsefulCount(reviewId);
                reviewStorage.removeDislikeFromReview(reviewId, userId);
                review.get().getDislikedBy().remove(userId);
            }
            reviewStorage.addLikeToReview(reviewId, userId);
            reviewStorage.addUsefulCount(reviewId);
            review.get().getLikedBy().add(userId);
        } else {
            if (user.isEmpty()) {
                throw new NotFoundException("Пользователь не найден");
            }
            if (review.isEmpty()) {
                throw new NotFoundException("Отзыв не найден");
            }
        }
    }

    public void removeLike(long reviewId, long userId) {
        Optional<User> user = userStorage.findUserById(userId);
        Optional<Review> review = reviewStorage.getReviewById(reviewId);
        if (!user.isEmpty() && !review.isEmpty()) {
            reviewStorage.removeLikeFromReview(reviewId, userId);
            reviewStorage.subtractUsefulCount(reviewId);
            review.get().getLikedBy().remove(userId);
        } else {
            if (user.isEmpty()) {
                throw new NotFoundException("Пользователь не найден");
            }
            if (review.isEmpty()) {
                throw new NotFoundException("Отзыв не найден");
            }
        }
    }

    public void dislikeReview(long reviewId, long userId) {
        Optional<User> user = userStorage.findUserById(userId);
        Optional<Review> review = reviewStorage.getReviewById(reviewId);
        if (!user.isEmpty() && !review.isEmpty()) {
            if (review.get().getLikedBy().contains(userId)) {
                reviewStorage.removeLikeFromReview(reviewId, userId);
                reviewStorage.subtractUsefulCount(reviewId);
                review.get().getLikedBy().remove(userId);
            }
            reviewStorage.addDislikeToReview(reviewId, userId);
            reviewStorage.subtractUsefulCount(reviewId);
            review.get().getDislikedBy().add(userId);
        } else {
            if (user.isEmpty()) {
                throw new NotFoundException("Пользователь не найден");
            }
            if (review.isEmpty()) {
                throw new NotFoundException("Отзыв не найден");
            }
        }
    }

    public void removeDislike(long reviewId, long userId) {
        Optional<User> user = userStorage.findUserById(userId);
        Optional<Review> review = reviewStorage.getReviewById(reviewId);
        if (!user.isEmpty() && !review.isEmpty()) {
            reviewStorage.removeDislikeFromReview(reviewId, userId);
            reviewStorage.addUsefulCount(reviewId);
            review.get().getDislikedBy().remove(userId);
        } else {
            if (user.isEmpty()) {
                throw new NotFoundException("Пользователь не найден");
            }
            if (review.isEmpty()) {
                throw new NotFoundException("Отзыв не найден");
            }
        }
    }
}
