package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    Film delete(Integer id);

    Collection<Film> getAll();

    Film getById(Integer id);

    Film addLike(Integer id, Integer userId);

    Film deleteLike(Integer id, Integer userId);
}
