package ru.yandex.practicum.filmorate.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;
import ru.yandex.practicum.filmorate.util.validator.annotations.NoSpace;

public class NoSpaceStringValidator implements ConstraintValidator<NoSpace, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return !value.contains(StringUtils.SPACE);
    }
}
