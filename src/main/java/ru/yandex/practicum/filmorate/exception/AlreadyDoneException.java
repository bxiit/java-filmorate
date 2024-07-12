package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class AlreadyDoneException extends RuntimeException {
    public AlreadyDoneException(String message) {
        super(message);
    }

    public AlreadyDoneException() {
        super();
    }
}
