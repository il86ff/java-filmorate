package ru.yandex.practicum.filmorate.storage.memory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> filmMap;
    private final Map<Integer, Integer> likes;
    private Integer id;

    public InMemoryFilmStorage() {
        likes = new HashMap<>();
        filmMap = new HashMap<>();
        id = 0;
    }

    @Override
    public Film add(Film film) {
        film.setId(++id);
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        filmMap.put(film.getId(), film);
        return film;
    }

    @Override
    public Film delete(Integer id) {
        return filmMap.remove(id);
    }

    @Override
    public Collection<Film> getAll() {
        return filmMap.values();
    }

    @Override
    public Film getById(Integer id) {
        return filmMap.get(id);
    }

    @Override
    public Film addLike(Integer id, Integer userId) {
        likes.put(id, userId);
        return filmMap.get(id);
    }

    @Override
    public Film deleteLike(Integer id, Integer userId) {
        likes.remove(id);
        return getById(id);
    }

    @Override
    public boolean exists(Integer id) {
        return false;
    }

    @Override
    public void addGenres(Integer filmId, Set<Genre> genres) {

    }

    @Override
    public void updateGenres(Integer filmId, Set<Genre> genres) {

    }

    @Override
    public Set<Genre> getGenres(Integer filmId) {
        return null;
    }

    @Override
    public void deleteGenres(Integer filmId) {

    }
}
