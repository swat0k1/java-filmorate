package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.dbStorage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.UserFeedDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewDbStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final UserFeedDbStorage userFeedDbStorage;

    @Transactional
    public Review addReview(Review review) {
        int userId = review.getUserId();
        int filmId = review.getFilmId();
        checkUserFilm(userId, filmId);
        review.setUseful(0);
        Review reviewDb = reviewStorage.addReview(review);
        userFeedDbStorage.save(new UserFeed(userId, EventType.REVIEW,
                Operation.ADD, reviewDb.getReviewId()));
        return reviewDb;
    }

    public Review getReview(int reviewId) {
        checkReview(reviewId);
        return reviewStorage.getReview(reviewId);
    }

    public List<Review> getReviewsByFilmId(int filmId, int count) {
        return reviewStorage.getReviewsByFilmId(filmId).stream()
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .limit(count)
                .toList();
    }

    public List<Review> getReviews(Integer filmId, int count) {
        return (filmId == null)
                ? reviewStorage.findAllOrderedLimited(count)
                : reviewStorage.findByFilmOrderedLimited(filmId, count);
    }

    @Transactional
    public Review updateReview(Review review) {
        int newReviewId = review.getReviewId();
        int filmId = review.getFilmId();
        int userId = review.getUserId();
        Review reviewDb = getReview(newReviewId);
        checkUserFilm(userId, filmId);

        reviewDb.setContent(review.getContent());
        reviewDb.setIsPositive(review.getIsPositive());
        reviewDb.setUseful(reviewStorage.getAmountOfLikes(newReviewId) - reviewStorage.getAmountOfDislikes(newReviewId));
        Review updatedReview = reviewStorage.updateReview(reviewDb);
        userFeedDbStorage.save(new UserFeed(userId, EventType.REVIEW,
                Operation.UPDATE, newReviewId));
        return updatedReview;

    }

    public void deleteReviewById(int reviewId) {
        Review review = reviewStorage.getReview(reviewId);
        reviewStorage.deleteAllLikesByReviewId(reviewId);
        reviewStorage.deleteReviewById(reviewId);
    }

    @Transactional
    public Review addUsersLike(int reviewId, int userId) {

        checkReviewUser(reviewId, userId);
        if (reviewStorage.hasUsersLike(reviewId, userId)) {
            return getReview(reviewId);
        } else if (reviewStorage.hasUsersDislike(reviewId, userId)) {
            userFeedDbStorage.save(new UserFeed(userId, EventType.LIKE,
                    Operation.REMOVE, reviewId));
            reviewStorage.deleteUsersDislike(reviewId, userId);
            return getReview(reviewId);
        }

        reviewStorage.addUsersLike(reviewId, userId);
        userFeedDbStorage.save(new UserFeed(userId, EventType.LIKE,
                Operation.ADD, reviewId));

        return calculateUseful(reviewId);
    }

    public Review addUsersDislike(int reviewId, int userId) {

        checkReviewUser(reviewId, userId);
        if (reviewStorage.hasUsersDislike(reviewId, userId)) {
            return getReview(reviewId);
        } else if (reviewStorage.hasUsersLike(reviewId, userId)) {
            reviewStorage.deleteUsersLike(reviewId, userId);
        }

        reviewStorage.addUsersDislike(reviewId, userId);
        userFeedDbStorage.save(new UserFeed(userId, EventType.LIKE,
                Operation.REMOVE, reviewId));

        return calculateUseful(reviewId);

    }

    public Review deleteUsersLike(int reviewId, int userId) {

        checkReviewUser(reviewId, userId);

        if (reviewStorage.hasUsersLike(reviewId, userId)) {
            reviewStorage.deleteUsersLike(reviewId, userId);
        }
        return calculateUseful(reviewId);

    }

    public Review deleteUsersDislike(int reviewId, int userId) {

        checkReviewUser(reviewId, userId);

        if (reviewStorage.hasUsersDislike(reviewId, userId)) {
            reviewStorage.deleteUsersDislike(reviewId, userId);
        }

        return calculateUseful(reviewId);

    }

    private Review calculateUseful(int reviewId) {

        Review review = getReview(reviewId);
        review.setUseful(reviewStorage.getAmountOfLikes(reviewId) - reviewStorage.getAmountOfDislikes(reviewId));
        return reviewStorage.updateReview(review);

    }

    private void checkUserFilm(int userId, int filmId) {

        if (userId == 0) {
            throw new ValidationException("Ошибка id пользователя");
        }

        if (filmId == 0) {
            throw new ValidationException("Ошибка id фильма");
        }

        if (userStorage.getUserById(userId) == null) {
            throw new InternalServerException("Пользователь id = " + userId + " не найден!");
        }

        if (filmStorage.getFilmById(filmId) == null) {
            throw new InternalServerException("Фильм id = " + userId + " не найден!");
        }

    }

    private void checkReviewUser(int reviewId, int userId) {
        try {
            Optional<Review> reviewResult = Optional.ofNullable(reviewStorage.getReview(reviewId));
            if (reviewResult.isEmpty()) {
                throw new InternalServerException("Отзыв id = " + reviewId + " не найден!");
            }
        } catch (EmptyResultDataAccessException ex) {
            throw new InternalServerException("Отзыв id = " + reviewId + " не найден!");
        }

        if (userStorage.getUserById(userId) == null) {
            throw new InternalServerException("Пользователь id = " + userId + " не найден!");
        }
    }

    private void checkReview(int id) {
        try {
            Optional<Review> result = Optional.ofNullable(reviewStorage.getReview(id));
            if (result.isEmpty()) {
                throw new InternalServerException("Отзыв id = " + id + " не найден!");
            }
        } catch (EmptyResultDataAccessException ex) {
            throw new InternalServerException("Отзыв id = " + id + " не найден!");
        }
    }

}
