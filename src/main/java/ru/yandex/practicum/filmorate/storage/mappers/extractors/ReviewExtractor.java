package ru.yandex.practicum.filmorate.storage.mappers.extractors;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ReviewExtractor implements ResultSetExtractor<List<Review>> {

    @Override
    public List<Review> extractData(ResultSet rs) throws SQLException {
        Map<Long, Review> reviewIdToReview = new HashMap<>();
        while (rs.next()) {
            long reviewId = rs.getLong("reviewId");
            if (reviewIdToReview.containsKey(reviewId)) {
                long likeUserId = rs.getLong("like_user_id");
                if (likeUserId != 0) {
                    reviewIdToReview.get(reviewId).getLikedBy().add(likeUserId);
                }
                long dislikeUserId = rs.getLong("dislike_user_id");
                if (dislikeUserId != 0) {
                    reviewIdToReview.get(reviewId).getDislikedBy().add(dislikeUserId);
                }
            } else {
                Review review = new Review();
                review.setReviewId(reviewId);
                review.setUserId(rs.getLong("userId"));
                review.setFilmId(rs.getLong("filmId"));
                review.setContent(rs.getString("content"));
                review.setIsPositive(rs.getBoolean("isPositive"));
                review.setUseful(rs.getLong("useful"));
                long likeUserId = rs.getLong("like_user_id");
                if (likeUserId != 0) {
                    review.getLikedBy().add(likeUserId);
                }
                long dislikeUserId = rs.getLong("dislike_user_id");
                if (dislikeUserId != 0) {
                    review.getDislikedBy().add(dislikeUserId);
                }
                reviewIdToReview.put(reviewId, review);
            }
        }
        return new ArrayList<>(reviewIdToReview.values());
    }
}
