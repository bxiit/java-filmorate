package ru.yandex.practicum.filmorate.util.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.util.validator.DurationValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DurationValidator.class)
public @interface MinDuration {
    String message() default "Продолжительность фильма не может быть меньше нуля";

    // seconds
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    long value();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
