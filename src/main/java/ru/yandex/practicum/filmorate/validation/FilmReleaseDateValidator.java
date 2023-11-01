package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FilmReleaseDateValidator implements ConstraintValidator<ValidFilmReleaseDate, LocalDate> {
    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return date.isAfter(LocalDate.of(1895, 12,28)) || date.isEqual(LocalDate.of(1895, 12,28));
    }

    @Override
    public void initialize(ValidFilmReleaseDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

}
