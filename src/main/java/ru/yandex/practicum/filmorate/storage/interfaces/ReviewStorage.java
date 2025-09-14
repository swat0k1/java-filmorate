package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    public Review getReview(int reviewId);

    public List<Review> getAllReviews();

    public List<Review> getReviewsByFilmId(int filmId);

    public Review addReview(Review review);

    public Review updateReview(Review review);

    public void deleteReviewById(int reviewId);

    public int getAmountOfLikes(int reviewId);

    public int getAmountOfDislikes(int reviewId);

    public boolean hasUsersLike(int reviewId, int userId);

    public boolean hasUsersDislike(int reviewId, int userId);

    public void addUsersLike(int reviewId, int userId);

    public void addUsersDislike(int reviewId, int userId);

    public void deleteUsersLike(int reviewId, int userId);

    public void deleteUsersDislike(int reviewId, int userId);

    public void deleteAllLikesByReviewId(int reviewId);
}
