package ru.yandex.practicum.filmorate.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.AlreadyDoneException;
import ru.yandex.practicum.filmorate.exception.ConflictException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.TemporarilyNotAvailableException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.net.URISyntaxException;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidationException(ValidationException e) throws URISyntaxException {
        log.error(e.getMessage());

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException e) {
        log.error(e.getMessage());
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    // Валидация аннотацией в моделях
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleThrowable(MethodArgumentNotValidException e) {
        log.error(e.getMessage());

        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(AlreadyDoneException.class)
    public ProblemDetail handleUserAlreadyExist(final AlreadyDoneException e) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                e.getMessage()
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException e) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                e.getMessage()
        );
    }

    @ExceptionHandler(TemporarilyNotAvailableException.class)
    public ProblemDetail handleTemporarilyNotAvailableException(TemporarilyNotAvailableException e) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_ACCEPTABLE,
                e.getMessage()
        );
    }
}
