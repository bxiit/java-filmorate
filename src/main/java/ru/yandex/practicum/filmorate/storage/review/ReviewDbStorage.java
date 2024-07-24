package ru.yandex.practicum.filmorate.storage.review;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;
import java.util.Optional;

@Repository
@Qualifier("ReviewDbStorage")
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {

    private static final String ADD_REVIEW_QUERY = "INSERT INTO review (userId, filmId, content, isPositive) " +
                                                   "VALUES ( ?, ?, ?, ?);";
    private static final String UPDATE_REVIEW_QUERY = "UPDATE review SET content = ?, isPositive = ? WHERE reviewId = ?";
    private static final String DELETE_REVIEW_QUERY = "DELETE FROM review WHERE reviewId = ?";
    private static final String GET_REVIEW_BY_ID_QUERY = "SELECT * FROM review AS r LEFT JOIN review_user_likes AS rul ON r.reviewId = rul.review_id " +
                                                         "LEFT JOIN review_user_dislikes AS rud on r.reviewId = rud.review_id " +
                                                         "WHERE reviewId = ?";
    private static final String GET_ALL_REVIEWS_QUERY = "SELECT * FROM review AS r LEFT JOIN review_user_likes AS rul ON r.reviewId = rul.review_id " +
                                                        "LEFT JOIN review_user_dislikes AS rud on r.reviewId = rud.review_id";
    private static final String GET_ALL_REVIEWS_BY_FILM_ID_QUERY = "SELECT * FROM review AS r LEFT JOIN review_user_likes AS rul ON r.reviewId = rul.review_id " +
                                                                   "LEFT JOIN review_user_dislikes AS rud on r.reviewId = rud.review_id " +
                                                                   "WHERE filmId = ?";
    private static final String ADD_USEFUL_QUERY = "UPDATE review SET useful = useful + 1 WHERE reviewId = ?";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO review_user_likes VALUES (?, ?)";
    private static final String SUBTRACT_USEFUL_QUERY = "UPDATE review SET useful = useful - 1 WHERE reviewId = ?";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM review_user_likes WHERE review_id = ? AND like_user_id = ?";
    private static final String INSERT_DISLIKE_QUERY = "INSERT INTO review_user_dislikes VALUES (?, ?)";
    private static final String DELETE_DISLIKE_QUERY = "DELETE FROM review_user_dislikes WHERE review_id = ? AND dislike_user_id = ?";

    @Autowired
    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> rowMapper, ResultSetExtractor<List<Review>> extractor) {
        super(jdbc, rowMapper, extractor);
    }

    @Override
    public Review addReview(Review review) {
        long id = 0;
        id = insert(
                ADD_REVIEW_QUERY,
                review.getUserId(),
                review.getFilmId(),
                review.getContent(),
                review.getIsPositive()
        );
        review.setReviewId(id);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        update(
                UPDATE_REVIEW_QUERY,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );
        return review;
    }

    @Override
    public boolean deleteReview(long reviewId) {
        return delete(DELETE_REVIEW_QUERY, reviewId);
    }

    @Override
    public Optional<Review> getReviewById(long reviewId) {
        return findOneWithExtractor(GET_REVIEW_BY_ID_QUERY, reviewId);
    }

    @Override
    public List<Review> getAllReviews() {
        return findManyWithExtractor(GET_ALL_REVIEWS_QUERY);
    }

    @Override
    public List<Review> getAllReviewsByFilmId(long filmId) {
        return findManyWithExtractor(GET_ALL_REVIEWS_BY_FILM_ID_QUERY, filmId);
    }

    @Override
    public void addLikeToReview(long reviewId, long userId) {
        noPkInsert(INSERT_LIKE_QUERY, reviewId, userId);

    }

    @Override
    public void removeLikeFromReview(long reviewId, long userId) {
        delete(DELETE_LIKE_QUERY, reviewId, userId);

    }

    @Override
    public void addDislikeToReview(long reviewId, long userId) {
        noPkInsert(INSERT_DISLIKE_QUERY, reviewId, userId);
    }

    @Override
    public void removeDislikeFromReview(long reviewId, long userId) {
        delete(DELETE_DISLIKE_QUERY, reviewId, userId);

    }

    @Override
    public void addUsefulCount(long reviewId) {
        update(ADD_USEFUL_QUERY, reviewId);
    }

    @Override
    public void subtractUsefulCount(long reviewId) {
        update(SUBTRACT_USEFUL_QUERY, reviewId);
    }
}
