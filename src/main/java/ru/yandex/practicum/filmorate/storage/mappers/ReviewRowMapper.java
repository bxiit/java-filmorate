package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ReviewRowMapper implements RowMapper<Review> {
    @Override
    public Review mapRow(ResultSet rs, int rowNum) throws SQLException {
        Review review = new Review();
        review.setReviewId(rs.getLong("reviewId"));
        review.setUserId(rs.getLong("userId"));
        review.setFilmId(rs.getLong("filmId"));
        review.setContent(rs.getString("content"));
        review.setIsPositive(rs.getBoolean("isPositive"));
        review.setUseful(rs.getLong("useful"));
        return review;
    }
}
