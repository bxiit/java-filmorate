package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewServiceFacade;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewServiceFacade reviewServiceFacade;

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        log.info(review.toString());
        return reviewServiceFacade.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info(review.toString());
        return reviewServiceFacade.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public boolean deleteReview(@PathVariable("id") long reviewId) {
        return reviewServiceFacade.deleteReview(reviewId);
    }

    @GetMapping("/{id}")
    public Optional<Review> getReviewById(@PathVariable("id") long reviewId) {
        return reviewServiceFacade.getReviewById(reviewId);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false, defaultValue = "0") long filmId,
                                   @RequestParam(required = false, defaultValue = "10") int count) {
        return reviewServiceFacade.getReviews(filmId, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeReview(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
        reviewServiceFacade.likeReview(reviewId, userId);
        log.info(reviewServiceFacade.getReviewById(reviewId).toString());
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void dislikeReview(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
        reviewServiceFacade.dislikeReview(reviewId, userId);
        log.info(reviewServiceFacade.getReviewById(reviewId).toString());
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
        reviewServiceFacade.removeLike(reviewId, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
        reviewServiceFacade.removeDislike(reviewId, userId);
    }
}
