package ru.yandex.practicum.filmorate.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.util.annotations.NotBefore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateValidator implements ConstraintValidator<NotBefore, LocalDate> {
    private LocalDate earliestDate;

    @Override
    public void initialize(NotBefore constraintAnnotation) {
        earliestDate = LocalDate.parse(constraintAnnotation.value(), DateTimeFormatter.ISO_DATE);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return earliestDate.isBefore(value);
    }
}
