package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDao;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaDao mpaDao;

    public Mpa getById(Integer id) {
        log.info("получение категории по id {}", id);
        if (!mpaDao.exist(id)) {
            throw new NotFoundException(String.format("Категории с id = %s не существует...", id));
        }
        return mpaDao.getById(id);
    }

    public Collection<Mpa> getAll() {
        log.info("получение списка всех категорий");
        return mpaDao.getAll();
    }
}
