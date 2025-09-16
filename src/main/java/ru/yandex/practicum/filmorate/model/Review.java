package ru.yandex.practicum.filmorate.model;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Review {

    private int reviewId;
    @NotBlank(message = "Отзыв должен содержать текст (content).")
    private String content;
    @NotNull(message = "Значение isPisitive должно быть заполнено")
    private Boolean isPositive;
    @NotNull(message = "Значение userId должно быть заполнено")
    private int userId;
    @NotNull(message = "Значение filmId должно быть заполнено")
    private int filmId;
    private int useful;

}
