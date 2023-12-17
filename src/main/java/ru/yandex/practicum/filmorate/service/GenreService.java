package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreDao genreDao;

    public Genre getById(Integer id) {
        log.info("получение жанра по id {}", id);
        if (!genreDao.exist(id)) {
            throw new NotFoundException(String.format("Жанра с id = %s не существует...", id));
        }
        return genreDao.getById(id);
    }

    public Collection<Genre> getAll() {
        log.info("получение списка всех жанров");
        return genreDao.getAll();
    }
}
