package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {

    private int id;
    @NotBlank(message = "Название не может быть пустым.")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
    private String description;
    @PastOrPresent(message = "Дата релиза не может быть больше текущей даты.")
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private FilmRating rating;

    private Set<Integer> likes = new HashSet<>();
    private Set<FilmGenre> genres = new HashSet<>();

}
