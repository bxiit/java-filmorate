package ru.yandex.practicum.filmorate.exception.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private int status;
    private String message;
    private HttpStatus httpStatus;
}
