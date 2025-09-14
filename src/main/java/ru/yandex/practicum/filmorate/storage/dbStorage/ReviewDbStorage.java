package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FindingException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.util.List;

@Slf4j
@Repository
public class ReviewDbStorage extends BaseRepository<Review> implements ReviewStorage {

    private static final String INSERT_REVIEW = "INSERT " +
            "INTO reviews (content, is_positive, user_id, film_id, useful)" +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String GET_BY_ID = "SELECT *" +
            "FROM reviews" +
            "WHERE review_id = ?";

    private static final String GET_BY_FILM_ID = "SELECT *" +
            "FROM reviews" +
            "WHERE film_id";

    private static final String GET_ALL = "SELECT *" +
            "FROM reviews";

    private static final String UPDATE_REVIEW = "UPDATE reviews " +
            "SET content = ?, is_positive = ?, useful = ?" +
            "WHERE review_id = ?";

    private static final String DELETE_BY_ID = "DELETE" +
            "FROM reviews" +
            "WHERE review_id = ?";

    private static final String ADD_LIKE = "INSERT " +
            "INTO review_likes (review_id, user_id, is_like) " +
            "VALUES (?, ?, true)";

    private static final String ADD_DISLIKE = "INSERT " +
            "INTO review_likes (review_id, user_id, is_like) " +
            "VALUES (?, ?, false)";

    private static final String COUNT_LIKES = "SELECT COUNT(*)" +
            "FROM review_likes" +
            "WHERE review_id = ? AND is_like = true";

    private static final String COUNT_DISLIKES = "SELECT COUNT(*)" +
            "FROM review_likes" +
            "WHERE review_id = ? AND is_like = false";

    private static final String HAS_LIKE = "SELECT EXISTS(" +
            "SELECT 1 " +
            "FROM review_likes " +
            "WHERE review_id = ? AND user_id = ? AND is_like = true" +
            ")";

    private static final String HAS_DISLIKE = "SELECT EXISTS(" +
            "SELECT 1 " +
            "FROM review_likes " +
            "WHERE review_id = ? AND user_id = ? AND is_like = false" +
            ")";

    private static final String DELETE_LIKE = "DELETE " +
            "FROM review_likes " +
            "WHERE review_id = ? AND user_id = ? AND is_like = true";

    private static final String DELETE_DISLIKE = "DELETE " +
            "FROM review_likes " +
            "WHERE review_id = ? AND user_id = ? AND is_like = true";

    private static final String DELETE_ALL_LIKES = "DELETE " +
            "FROM review_likes " +
            "WHERE review_id = ?";

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper, Review.class);
    }

    @Override
    public Review getReview(int reviewId) {

        try {
            return findOne(GET_BY_ID, reviewId)
                    .orElseThrow(() -> new FindingException("Отзыв с id = " + reviewId + " не найден"));
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка получения отзыва.");
        }

    }

    @Override
    public List<Review> getAllReviews() {

        try {
            return findMany(GET_ALL);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка получения отзывов.");
        }

    }

    @Override
    public List<Review> getReviewsByFilmId(int filmId) {

        try {
            return findMany(GET_BY_FILM_ID, filmId);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка получения отзывов.");
        }

    }

    @Override
    public Review addReview(Review review) {

        try {
            int id = insert(
                    INSERT_REVIEW,
                    review.getContent(),
                    review.getIsPositive(),
                    review.getUserId(),
                    review.getFilmId(),
                    review.getUseful());

            review.setReviewId(id);

            return review;

        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка добавления отзыва.");
        }

    }

    @Override
    public Review updateReview(Review review) {

        try {
            update(UPDATE_REVIEW,
                    review.getContent(),
                    review.getIsPositive(),
                    review.getUseful(),
                    review.getUserId());

            return getReview(review.getReviewId());

        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка обновления отзыва.");
        }

    }

    @Override
    public void deleteReviewById(int reviewId) {

        try {
            update(DELETE_BY_ID, reviewId);
        } catch (InternalServerException e) {
            throw new FindingException("Ошибка удаления отзыва.");
        }

    }

    @Override
    public int getAmountOfLikes(int reviewId) {

        try {
            return jdbc.queryForObject(COUNT_LIKES, Integer.class, reviewId);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка получения количества лайков.");
        }

    }

    @Override
    public int getAmountOfDislikes(int reviewId) {

        try {
            return jdbc.queryForObject(COUNT_DISLIKES, Integer.class, reviewId);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка получения количества дислайков.");
        }

    }

    @Override
    public boolean hasUsersLike(int reviewId, int userId) {

        try {
            return Boolean.TRUE.equals(jdbc.queryForObject(HAS_LIKE, Boolean.class, reviewId, userId));
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка проверки наличия лайка.");
        }

    }

    @Override
    public boolean hasUsersDislike(int reviewId, int userId) {

        try {
            return Boolean.TRUE.equals(jdbc.queryForObject(HAS_DISLIKE, Boolean.class, reviewId, userId));
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка проверки наличия дислайка.");
        }

    }

    @Override
    public void addUsersLike(int reviewId, int userId) {

        try {
            insert(ADD_LIKE, reviewId, userId);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка добавления лайка.");
        }

    }

    @Override
    public void addUsersDislike(int reviewId, int userId) {

        try {
            insert(ADD_DISLIKE, reviewId, userId);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка добавления дислайка.");
        }

    }

    @Override
    public void deleteUsersLike(int reviewId, int userId) {

        try {
            jdbc.update(DELETE_LIKE, reviewId, userId);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка удаления лайка.");
        }

    }

    @Override
    public void deleteUsersDislike(int reviewId, int userId) {

        try {
            jdbc.update(DELETE_DISLIKE, reviewId, userId);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка удаления дислайка.");
        }

    }

    @Override
    public void deleteAllLikesByReviewId(int reviewId) {

        try {
            jdbc.update(DELETE_ALL_LIKES, reviewId);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка удаления лайков.");
        }

    }
}
