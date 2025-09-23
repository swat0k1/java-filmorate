package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@AllArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(@Valid @RequestBody @NotNull Review review) {
        return reviewService.addReview(review);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") @NotNull int id) {
        return reviewService.getReview(id);
    }

    @GetMapping
    public List<Review> getReviews(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(defaultValue = "10") Integer count
    ) {
        int limit = Math.max(1, count);
        return reviewService.getReviews(filmId, limit);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody @NotNull Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable("id") @NotNull int id) {
        reviewService.deleteReviewById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Review addLike(@PathVariable("id") @NotNull int id,
                          @PathVariable("userId") @NotNull int userId) {
        return reviewService.addUserLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislike(@PathVariable("id") @NotNull int id,
                             @PathVariable("userId") @NotNull int userId) {
        return reviewService.addUserDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review removeLike(@PathVariable("id") @NotNull int id,
                             @PathVariable("userId") @NotNull int userId) {
        return reviewService.deleteUserLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review removeDislike(@PathVariable("id") @NotNull int id,
                                @PathVariable("userId") @NotNull int userId) {
        return reviewService.deleteUserDislike(id, userId);
    }

}
