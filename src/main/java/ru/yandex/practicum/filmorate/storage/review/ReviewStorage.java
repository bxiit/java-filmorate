package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {
    Review addReview(Review review);

    Review updateReview(Review review);

    boolean deleteReview(long reviewId);

    Optional<Review> getReviewById(long reviewId);

    List<Review> getAllReviews();

    List<Review> getAllReviewsByFilmId(long filmId);

    void addLikeToReview(long reviewId, long userId);

    void removeLikeFromReview(long reviewId, long userId);

    void addDislikeToReview(long reviewId, long userId);

    void removeDislikeFromReview(long reviewId, long userId);

    void addUsefulCount(long reviewId);

    void subtractUsefulCount(long reviewId);
}
