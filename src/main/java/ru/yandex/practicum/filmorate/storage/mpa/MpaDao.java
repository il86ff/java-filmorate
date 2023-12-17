package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaDao {
    Mpa getById(Integer id);

    Collection<Mpa> getAll();

    boolean exist(Integer id);

}
