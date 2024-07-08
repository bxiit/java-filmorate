package ru.yandex.practicum.filmorate.util.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.springframework.format.annotation.DateTimeFormat;
import ru.yandex.practicum.filmorate.util.validator.LocalDateValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LocalDateValidator.class)
public @interface NotBefore {
    String message() default "Дата релиза фильма слишком старая";

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    String value();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
