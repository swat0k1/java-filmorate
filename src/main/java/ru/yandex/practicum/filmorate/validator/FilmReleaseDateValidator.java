package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class FilmReleaseDateValidator implements ConstraintValidator<FilmReleaseDateCheck, LocalDate> {

    private LocalDate firstMovie = LocalDate.of(1895, 12, 28);

    @Override
    public void initialize(FilmReleaseDateCheck dateConstraint) {
    }

    @Override
    public boolean isValid(LocalDate filmReleaseDate, ConstraintValidatorContext cxt) {
        return firstMovie.isBefore(filmReleaseDate)
                || firstMovie.isEqual(filmReleaseDate);
    }
}
