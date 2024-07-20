package ru.yandex.practicum.filmorate.service.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.Operation;
import ru.yandex.practicum.filmorate.service.event.EventService;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReviewServiceFacade {
    private final ReviewService reviewService;
    private final EventService eventService;

    public Review addReview(Review review) {
        return reviewService.addReview(review);
    }

    public Review updateReview(Review review) {
        return reviewService.updateReview(review);
    }

    public boolean deleteReview(long reviewId) {
        return reviewService.deleteReview(reviewId);
    }

    public List<Review> getReviews(long filmId, int count) {
        return reviewService.getReviews(filmId, count);
    }

    public Optional<Review> getReviewById(long reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    public void likeReview(long reviewId, long userId) {
        reviewService.likeReview(reviewId, userId);
        eventService.addEvent(userId, reviewId, EventType.LIKE, Operation.ADD);
    }

    public void removeLike(long reviewId, long userId) {
        reviewService.removeLike(reviewId, userId);
        eventService.addEvent(userId, reviewId, EventType.LIKE, Operation.REMOVE);
    }

    public void dislikeReview(long reviewId, long userId) {
        reviewService.dislikeReview(reviewId, userId);
    }

    public void removeDislike(long reviewId, long userId) {
        reviewService.removeDislike(reviewId, userId);
    }
}
