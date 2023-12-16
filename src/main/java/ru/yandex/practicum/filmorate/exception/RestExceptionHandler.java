package ru.yandex.practicum.filmorate.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import ru.yandex.practicum.filmorate.controller.FilmController;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
public class RestExceptionHandler extends ExceptionHandlerExceptionResolver {
    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("Ошибки валидации тела запроса: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler({NotFoundException.class, EmptyResultDataAccessException.class})
    public ResponseEntity<Set<String>> handleException(NotFoundException exception) {
        Set<String> messages = new HashSet<>();
        messages.add(exception.getMessage());
        log.error("Ошибка поиска: {}", messages);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(messages);
    }

    @ExceptionHandler({IdIsNullException.class, DuplicateIdException.class, IllegalArgumentException.class})
    public ResponseEntity<Set<String>> handleException(Exception exception) {
        Set<String> messages = new HashSet<>();
        messages.add(exception.getMessage());
        log.error("Ошибка заполнения тела запроса: {}", messages);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messages);
    }
}
