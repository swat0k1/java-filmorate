package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FindingException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Slf4j
@Repository
public class ReviewDbStorage extends BaseRepository<Review> {

    private static final String INSERT_REVIEW = "INSERT " +
            "INTO reviews (content, is_positive, user_id, film_id, useful) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String GET_BY_ID = "SELECT * " +
            "FROM reviews " +
            "WHERE review_id = ?";

    private static final String GET_BY_FILM_ID = "SELECT * " +
            "FROM reviews " +
            "WHERE film_id = ?";

    private static final String GET_ALL = "SELECT * " +
            "FROM reviews";

    private static final String UPDATE_REVIEW = "UPDATE reviews " +
            "SET content = ?, is_positive = ?, useful = ? " +
            "WHERE review_id = ?";

    private static final String DELETE_BY_ID = "DELETE " +
            "FROM reviews " +
            "WHERE review_id = ?";

    private static final String ADD_LIKE_DISLIKE = "INSERT " +
            "INTO review_likes (review_id, user_id, is_like) " +
            "VALUES (?, ?, ?)";

    private static final String COUNT_LIKES_DISLIKES = "SELECT COUNT(*) " +
            "FROM review_likes " +
            "WHERE review_id = ? AND is_like = ?";

    private static final String HAS_LIKE_DISLIKE = "SELECT EXISTS(" +
            "SELECT 1 " +
            "FROM review_likes " +
            "WHERE review_id = ? AND user_id = ? AND is_like = ?" +
            ")";

    private static final String DELETE_LIKE_DISLIKE = "DELETE " +
            "FROM review_likes " +
            "WHERE review_id = ? AND user_id = ? AND is_like = ?";

    private static final String DELETE_ALL_LIKES = "DELETE " +
            "FROM review_likes " +
            "WHERE review_id = ?";

    public ReviewDbStorage(JdbcTemplate jdbc, RowMapper<Review> mapper) {
        super(jdbc, mapper, Review.class);
    }

    public Review getReview(int reviewId) {

        try {
            return findOne(GET_BY_ID, reviewId)
                    .orElseThrow(() -> new FindingException("Отзыв с id = " + reviewId + " не найден"));
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка получения отзыва.");
        }

    }

    public List<Review> getAllReviews() {

        try {
            return findMany(GET_ALL);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка получения отзывов.");
        }

    }

    public List<Review> getReviewsByFilmId(int filmId) {

        try {
            return findMany(GET_BY_FILM_ID, filmId);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка получения отзывов.");
        }

    }

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

    public Review updateReview(Review review) {

        try {
            update(UPDATE_REVIEW,
                    review.getContent(),
                    review.getIsPositive(),
                    review.getUseful(),
                    review.getReviewId());

            return getReview(review.getReviewId());

        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка обновления отзыва.");
        }

    }

    public void deleteReviewById(int reviewId) {

        try {
            update(DELETE_BY_ID, reviewId);
        } catch (InternalServerException e) {
            throw new FindingException("Ошибка удаления отзыва.");
        }

    }

    public int getAmountOfLikes(int reviewId) {

        try {
            return jdbc.queryForObject(COUNT_LIKES_DISLIKES, Integer.class, reviewId, true);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка получения количества лайков.");
        }

    }

    public int getAmountOfDislikes(int reviewId) {

        try {
            return jdbc.queryForObject(COUNT_LIKES_DISLIKES, Integer.class, reviewId, false);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка получения количества дислайков.");
        }

    }

    public boolean hasUsersLike(int reviewId, int userId) {

        try {
            return Boolean.TRUE.equals(jdbc.queryForObject(HAS_LIKE_DISLIKE, Boolean.class, reviewId, userId, true));
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка проверки наличия лайка.");
        }

    }

    public boolean hasUsersDislike(int reviewId, int userId) {

        try {
            return Boolean.TRUE.equals(jdbc.queryForObject(HAS_LIKE_DISLIKE, Boolean.class, reviewId, userId, false));
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка проверки наличия дислайка.");
        }

    }

    public void addUsersLike(int reviewId, int userId) {

        try {
            update(ADD_LIKE_DISLIKE, reviewId, userId, true);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка добавления лайка.");
        }

    }

    public void addUsersDislike(int reviewId, int userId) {

        try {
            update(ADD_LIKE_DISLIKE, reviewId, userId, false);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка добавления дислайка.");
        }

    }

    public void deleteUsersLike(int reviewId, int userId) {

        try {
            jdbc.update(DELETE_LIKE_DISLIKE, reviewId, userId, true);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка удаления лайка.");
        }

    }

    public void deleteUsersDislike(int reviewId, int userId) {

        try {
            jdbc.update(DELETE_LIKE_DISLIKE, reviewId, userId, false);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка удаления дислайка.");
        }

    }

    public void deleteAllLikesByReviewId(int reviewId) {

        try {
            jdbc.update(DELETE_ALL_LIKES, reviewId);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка удаления лайков.");
        }

    }
}
