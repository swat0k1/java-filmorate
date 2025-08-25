package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;
import ru.yandex.practicum.filmorate.validator.FilmReleaseDateCheck;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Film {

    private int id;
    @NotBlank(message = "Название не может быть пустым.")
    private String name;
    @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
    private String description;
    @PastOrPresent(message = "Дата релиза не может быть больше текущей даты.")
    @FilmReleaseDateCheck
    private LocalDate releaseDate;
    @Positive
    private int duration;
    private Mpa mpa;

    private Collection<Integer> likes = new HashSet<>();
    private Set<Genre> genres = new HashSet<>();

    /*
    public Film(int id, String name, String description, LocalDate releaseDate, int duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }
     */

}
