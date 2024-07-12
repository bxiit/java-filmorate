package ru.yandex.practicum.filmorate.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.yandex.practicum.filmorate.util.annotations.MinDuration;

import java.time.Duration;

public class DurationValidator implements ConstraintValidator<MinDuration, Duration> {

    private Duration duration;

    @Override
    public void initialize(MinDuration constraintAnnotation) {
        duration = Duration.ofSeconds(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(Duration value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        int result = value.compareTo(duration);
        return result > 0;
    }
}
