package ru.yandex.practicum.filmorate.util.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.TemporarilyNotAvailableException;

@Aspect
@Component
public class TemporarilyNotAvailableAspect {

    @Before("@annotation(ru.yandex.practicum.filmorate.util.annotations.TemporarilyUnavailable)")
    public void beforeAdvice() {
        throw new TemporarilyNotAvailableException("Функция пока что недоступна");
    }
}
