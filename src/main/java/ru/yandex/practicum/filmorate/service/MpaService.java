package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDao;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDao mpaDao;

    public Mpa getById(Integer id) {
        if (!mpaDao.exist(id)) {
            throw new NotFoundException(String.format("Категории с id = %s не существует...", id));
        }
        return mpaDao.getById(id);
    }

    public Collection<Mpa> getAll() {
        return mpaDao.getAll();
    }
}
