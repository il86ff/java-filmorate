package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
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
}
