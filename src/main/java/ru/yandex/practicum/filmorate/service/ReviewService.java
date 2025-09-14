package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.interfaces.ReviewStorage;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    //TODO: возможно тут нужно будет добавить что то связанное с лентой событий

    public Review addReview(Review review) {

        review.setUseful(0);
        Review reviewDb = reviewStorage.addReview(review);

        //TODO: место для реализации ленты событий

        return reviewDb;
    }

    public Review getReview(int reviewId) {
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

        if (reviewStorage.hasUsersDislike(reviewId, userId)) {
            return getReview(reviewId);
        } else if (reviewStorage.hasUsersLike(reviewId, userId)) {
            reviewStorage.deleteUsersLike(reviewId, userId);
        }

        reviewStorage.addUsersDislike(reviewId, userId);

        return calculateUseful(reviewId);

    }

    public Review deleteUsersLike(int reviewId, int userId) {

        if (reviewStorage.hasUsersLike(reviewId, userId)) {
            reviewStorage.deleteUsersLike(reviewId, userId);
            //TODO: место для реализации ленты событий
        }
        return calculateUseful(reviewId);

    }

    public Review deleteUsersDislike(int reviewId, int userId) {

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

}
