package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final HashMap<Integer, Film> films = new HashMap<>();
    private int id = 0;

    @GetMapping
    public Collection<Film> returnAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Добавляем фильм {}", film);
        if (films.containsKey(film.getId())) {
            log.info("фильм с id = {} уже существует...", film.getId());
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        film.setId(++id);
        films.put(id, film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Обновляем фильм {}", film);
        if (film.getId() == null) {
            log.warn("id фильма забыли передать...");
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        }
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм с id = {} не существует...", film.getId());
            throw new NotFoundException("Фильм не существует...");
        }
        films.put(film.getId(), film);
        return film;
    }
}
