package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final static Logger log = LoggerFactory.getLogger(UserController.class);
    private final HashMap<Integer, User> users = new HashMap<>();
    private int id = 0;

    @GetMapping
    public Collection<User> returnAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Добавляем пользователя {}", user);
        if (users.containsKey(user.getId())) {
            log.warn("Пользователь с id = {} уже существует...", user.getId());
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        user.setId(++id);
        user.setName(user.getName() == null ? user.getLogin() : user.getName());
        users.put(id, user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Обновляем пользователя {}", user);
        if (user.getId() == null) {
            log.info("id пользователя забыли передать...");
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        if (!users.containsKey(user.getId())) {
            log.info("Пользователь с id = {} не существует...", user.getId());
            throw new NotFoundException("Пользователь не существует...");
        }
        users.put(user.getId(), user);
        return user;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<NotFoundException> handleException(NotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(exception);
    }
}
