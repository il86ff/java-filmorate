package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> filmMap = new HashMap<>();
    private Integer id = 0;

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
        Film filmToLike = filmMap.get(id);
        filmToLike.getLikesUserId().add(userId);
        return filmToLike;
    }

    @Override
    public Film deleteLike(Integer id, Integer userId) {
        Film filmToUnLike = filmMap.get(id);
        filmToUnLike.getLikesUserId().remove(userId);
        return filmToUnLike;
    }
}
