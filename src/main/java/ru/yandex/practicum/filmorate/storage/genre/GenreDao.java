package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreDao {
    Genre getById(Integer id);

    Collection<Genre> getAll();

    boolean exist(Integer id);
}
