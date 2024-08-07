package ru.yandex.practicum.filmorate.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.AlreadyDoneException;
import ru.yandex.practicum.filmorate.exception.ConflictException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.TemporarilyNotAvailableException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.util.enums.search.SearchBy;
import ru.yandex.practicum.filmorate.util.parser.EnumPropertyEditorSupport;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(SearchBy.class, EnumPropertyEditorSupport.forEnum(SearchBy.class));
    }

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidationException(ValidationException e) {
        log.debug("Получена ошибка валидации {}", e.getMessage(), e);
        return commonExceptionHandle(BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ProblemDetail handleNotFoundException(NotFoundException e) {
        log.debug("Получен статус 404 Not found {}", e.getMessage(), e);
        return commonExceptionHandle(NOT_FOUND, e.getMessage());
    }

    // Валидация аннотацией в моделях
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleThrowable(MethodArgumentNotValidException e) {
        log.debug("Получена ошибка валидации {}", e.getMessage(), e);
        String errorMessage = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(AlreadyDoneException.class)
    public ProblemDetail handleUserAlreadyExist(final AlreadyDoneException e) {
        log.debug("Получена ошибка: уже существует {}", e.getMessage(), e);
        return commonExceptionHandle(CONFLICT, e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ProblemDetail handleConflict(ConflictException e) {
        log.debug("Получен конфликт {}", e.getMessage(), e);
        return commonExceptionHandle(CONFLICT, e.getMessage());
    }

    @ExceptionHandler(TemporarilyNotAvailableException.class)
    public ProblemDetail handleTemporarilyNotAvailableException(TemporarilyNotAvailableException e) {
        log.debug("Получена ошибка: функция временно не доступна {}", e.getMessage(), e);
        return commonExceptionHandle(NOT_ACCEPTABLE, e.getMessage());
    }

    private ProblemDetail commonExceptionHandle(HttpStatus httpStatus, String message) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(httpStatus, message);
        problemDetail.setProperty("error", message);
        return problemDetail;
    }
}
