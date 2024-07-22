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
        review = reviewService.addReview(review);
        eventService.createEvent(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.ADD);
        return review;
    }

    public Review updateReview(Review review) {
        review = reviewService.updateReview(review);
        eventService.createEvent(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.UPDATE);
        return review;
    }

    public boolean deleteReview(long reviewId) {
        Review review = reviewService.getReviewById(reviewId).get();

        if (reviewService.deleteReview(reviewId)) {
            eventService.createEvent(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.REMOVE);
            return true;
        }
        return false;
    }

    public List<Review> getReviews(long filmId, int count) {
        return reviewService.getReviews(filmId, count);
    }

    public Optional<Review> getReviewById(long reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    public void likeReview(long reviewId, long userId) {
        reviewService.likeReview(reviewId, userId);
    }

    public void removeLike(long reviewId, long userId) {
        reviewService.removeLike(reviewId, userId);
    }

    public void dislikeReview(long reviewId, long userId) {
        reviewService.dislikeReview(reviewId, userId);
    }

    public void removeDislike(long reviewId, long userId) {
        reviewService.removeDislike(reviewId, userId);
    }
}
