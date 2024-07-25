package ru.yandex.practicum.filmorate.service.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
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
    public ReviewService(ReviewStorage reviewStorage, UserStorage userStorage, FilmStorage filmStorage) {
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
        if (review.getContent() == null) {
            throw new ValidationException("Отзыва не может быть без контента");
        }
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        reviewStorage.updateReview(review);
        return reviewStorage.getReviewById(review.getReviewId()).get();
    }

    public boolean deleteReview(long reviewId) {
        return reviewStorage.deleteReview(reviewId);
    }

    public List<Review> getReviews(long filmId, int count) {
        if (filmId != 0) {
            return reviewStorage.getAllReviewsByFilmId(filmId).stream()
                    .sorted(Review::compareByUseful)
                    .limit(count)
                    .toList();

        } else {
            return reviewStorage.getAllReviews().stream()
                    .sorted(Review::compareByUseful)
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
