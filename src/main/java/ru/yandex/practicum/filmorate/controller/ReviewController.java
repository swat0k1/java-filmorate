package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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

    /*
    @GetMapping
    public List<Review> getReviews(@RequestParam(value = "filmId", required = false) int filmId,
                                   @RequestParam(value = "count", defaultValue = "10") @Min(value = 1) int count) {
        return reviewService.getReviewsByFilmId(filmId, count);
    }
     */

    @GetMapping
    public List<Review> getReviews() {
        return reviewService.getReviews();
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
        return reviewService.addUsersLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public Review addDislike(@PathVariable("id") @NotNull int id,
                             @PathVariable("userId") @NotNull int userId) {
        return reviewService.addUsersDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Review removeLike(@PathVariable("id") @NotNull int id,
                             @PathVariable("userId") @NotNull int userId) {
        return reviewService.deleteUsersLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public Review removeDislike(@PathVariable("id") @NotNull int id,
                                @PathVariable("userId") @NotNull int userId) {
        return reviewService.deleteUsersDislike(id, userId);
    }

}
