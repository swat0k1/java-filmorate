package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class FilmReleaseDateValidator implements ConstraintValidator<FilmReleaseDateCheck, LocalDate> {

    LocalDate FIRST_MOVIE = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(FilmReleaseDateCheck dateConstraint) {
    }

    @Override
    public boolean isValid(LocalDate filmReleaseDate, ConstraintValidatorContext cxt) {
        return FIRST_MOVIE.isBefore(filmReleaseDate)
                || FIRST_MOVIE.isEqual(filmReleaseDate);
    }
}
