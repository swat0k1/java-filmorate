package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dbStorage.ReviewDbStorage;
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
    //TODO: возможно тут нужно будет добавить что то связанное с лентой событий

    public Review addReview(Review review) {

        checkUserFilm(review.getUserId(), review.getFilmId());

        review.setUseful(0);
        Review reviewDb = reviewStorage.addReview(review);

        //TODO: место для реализации ленты событий

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

    public Review updateReview(Review review) {

        int newReviewId = review.getReviewId();
        Review reviewDb = getReview(newReviewId);

        checkUserFilm(review.getUserId(), review.getFilmId());

        reviewDb.setContent(review.getContent());
        reviewDb.setIsPositive(review.getIsPositive());
        reviewDb.setUseful(reviewStorage.getAmountOfLikes(newReviewId) - reviewStorage.getAmountOfDislikes(newReviewId));

        Review updatedReview = reviewStorage.updateReview(reviewDb);

        //TODO: место для реализации ленты событий

        return updatedReview;

    }

    public void deleteReviewById(int reviewId) {

        Review reviewDb = reviewStorage.getReview(reviewId);

        reviewStorage.deleteAllLikesByReviewId(reviewId);

        //TODO: место для реализации ленты событий

        reviewStorage.deleteReviewById(reviewId);

    }

    public Review addUsersLike(int reviewId, int userId) {

        checkReviewUser(reviewId, userId);

        if (reviewStorage.hasUsersLike(reviewId, userId)) {
            return getReview(reviewId);
        } else if (reviewStorage.hasUsersDislike(reviewId, userId)) {
            reviewStorage.deleteUsersDislike(reviewId, userId);
            return getReview(reviewId);
        }

        reviewStorage.addUsersLike(reviewId, userId);

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

        return calculateUseful(reviewId);

    }

    public Review deleteUsersLike(int reviewId, int userId) {

        checkReviewUser(reviewId, userId);

        if (reviewStorage.hasUsersLike(reviewId, userId)) {
            reviewStorage.deleteUsersLike(reviewId, userId);
            //TODO: место для реализации ленты событий
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
