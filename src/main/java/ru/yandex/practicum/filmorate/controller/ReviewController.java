package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import java.util.List;
import java.util.Optional;

@RestController
@Slf4j
public class ReviewController {

	private final ReviewService reviewService;


	@Autowired
	public ReviewController(ReviewService userService) {
		this.reviewService = userService;

	}

	@PostMapping("/reviews")
	public Review addReview(@RequestBody Review review) {
		log.info(review.toString());
		return reviewService.addReview(review);
	}

	@PutMapping("/reviews")
	public Review updateReview(@RequestBody Review review) {
		log.info(review.toString());
		return reviewService.updateReview(review);
	}

	@DeleteMapping("/reviews/{id}")
	public boolean deleteReview(@PathVariable("id") long reviewId) {
		return reviewService.deleteReview(reviewId);
	}

	@GetMapping("/reviews/{id}")
	public Optional<Review> getReviewById(@PathVariable("id") long reviewId) {
		return reviewService.getReviewById(reviewId);
	}

	@GetMapping("/reviews")
	public List<Review> getReviews(@RequestParam(required = false, defaultValue = "0") long filmId,
								   @RequestParam(required = false, defaultValue = "10") int count) {
		return reviewService.getReviews(filmId, count);
	}

	@PutMapping("/reviews/{id}/like/{userId}")
	public void likeReview(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
		reviewService.likeReview(reviewId, userId);
		log.info(reviewService.getReviewById(reviewId).toString());
	}

	@PutMapping("/reviews/{id}/dislike/{userId}")
	public void dislikeReview(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
		reviewService.dislikeReview(reviewId, userId);
		log.info(reviewService.getReviewById(reviewId).toString());
	}

	@DeleteMapping("/reviews/{id}/like/{userId}")
	public void removeLike(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
		reviewService.removeLike(reviewId, userId);
	}

	@DeleteMapping("/reviews/{id}/dislike/{userId}")
	public void removeDislike(@PathVariable("id") long reviewId, @PathVariable("userId") long userId) {
		reviewService.removeDislike(reviewId, userId);

	}

}
