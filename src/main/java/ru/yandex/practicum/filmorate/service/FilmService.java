package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.DuplicateIdException;
import ru.yandex.practicum.filmorate.exception.IdIsNullException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final Logger log = LoggerFactory.getLogger(FilmController.class);

    private final FilmStorage filmStorage;

    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film add(Film film) {
        log.info("Добавляем фильм {}", film);
        if (filmStorage.getAll().contains(filmStorage.getById(film.getId()))) {
            log.info("фильм с id = {} уже существует...", film.getId());
            throw new DuplicateIdException(String.format("фильм с id = %s уже существует...", film.getId()));
        }
        return filmStorage.add(film);
    }

    public Film update(Film film) {
        log.info("Обновляем фильм {}", film);
        if (film.getId() == null) {
            log.warn("id фильма забыли передать...");
            throw new IdIsNullException("Ошибка обновления фильма... ID не передан");
        }
        if (!filmStorage.getAll().contains(filmStorage.getById(film.getId()))) {
            log.warn("Фильм с id = {} не существует...", film.getId());
            throw new NotFoundException("Фильм не существует...");
        }
        return filmStorage.update(film);
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film delete(Integer id) {
        log.info("Удаляем фильм с id {}", id);
        return filmStorage.delete(id);
    }

    public Film getById(Integer id) {
        if (filmStorage.getById(id) == null) {
            throw new NotFoundException(String.format("Фильма с id = %s не существует...", id));
        }
        return filmStorage.getById(id);
    }

    public Film addLike(Integer filmId, Integer userId) {
        log.info("Добавляем лайк пользователя с id {} у фильма id {}.", userId, filmId);
        return filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        log.info("Удаляем лайк пользователя с id {} у фильма id {}.", userId, filmId);
        List<Integer> idList = getAll().stream().map(Film::getId).collect(Collectors.toList());
        if (!idList.contains(filmId))
            throw new NotFoundException(String.format("Фильм с id = %s не найден", filmId));
        if (!getById(filmId).getLikesUserId().contains(userId))
            throw new NotFoundException(String.format("Лайк пользователя с id = %s, у фильма id = %s, не найден", userId, filmId));
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getMostPopular(Integer count) {
        log.info("Запрос популярных фильмов с параметром - колличество {}.", count);
        return getAll().stream()
                .sorted((o1, o2) -> o2.getLikesUserId().size() - o1.getLikesUserId().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
