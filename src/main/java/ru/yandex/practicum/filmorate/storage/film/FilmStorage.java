package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Set;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    Film delete(Integer id);

    Collection<Film> getAll();

    Film getById(Integer id);

    Film addLike(Integer id, Integer userId);

    Film deleteLike(Integer id, Integer userId);

    boolean exists(Integer id);

    void addGenres(Integer filmId, Set<Genre> genres);

    void updateGenres(Integer filmId, Set<Genre> genres);

    Set<Genre> getGenres(Integer filmId);

    void deleteGenres(Integer filmId);
}
