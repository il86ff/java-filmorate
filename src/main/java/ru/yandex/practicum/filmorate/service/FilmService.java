package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.DuplicateIdException;
import ru.yandex.practicum.filmorate.exception.IdIsNullException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.DBFilmStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDao;
import ru.yandex.practicum.filmorate.storage.like.LikeDao;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDao;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenreDao genreDao;
    private final MpaDao mpaDao;
    private final LikeDao likeDao;

    @Autowired
    public FilmService(@Qualifier("DBFilmStorage") DBFilmStorage filmStorage, GenreDao genreDao, MpaDao mpaDao, LikeDao likeDao) {
        this.filmStorage = filmStorage;
        this.genreDao = genreDao;
        this.mpaDao = mpaDao;
        this.likeDao = likeDao;
    }

    public Film add(Film film) {
        log.info("Добавляем фильм {}", film);
        if (film.getId() != null) {
            if (filmStorage.exists(film.getId())) {
                throw new DuplicateIdException(String.format("фильм с id = %s уже существует...", film.getId()));
            } else {
                throw new IllegalArgumentException("ID присваевается в БД...");
            }
        }

        Film newFilm = filmStorage.add(film);
        filmStorage.addGenres(newFilm.getId(), film.getGenres());
        newFilm.setGenres(filmStorage.getGenres(newFilm.getId()));
        newFilm.setMpa(mpaDao.getById(newFilm.getMpa().getId()));
        return newFilm;
    }

    public Film update(Film film) {
        log.info("Обновляем фильм {}", film);
        if (film.getId() == null) {
            throw new IdIsNullException(String.format("Ошибка обновления фильма %s. ID не передан", film));
        }
        if (!filmStorage.exists(film.getId())) {
            throw new NotFoundException(String.format("Фильм с id = %s не существует...", film.getId()));
        }
        Film newFilm = filmStorage.update(film);
        filmStorage.updateGenres(newFilm.getId(), film.getGenres());
        newFilm.setGenres(filmStorage.getGenres(newFilm.getId()));
        newFilm.setMpa(mpaDao.getById(newFilm.getMpa().getId()));
        return newFilm;
    }

    public Collection<Film> getAll() {
        log.info("полуение списка всех фильмов");
        Collection<Film> films = filmStorage.getAll();
        for (Film f : films) {
            f.setMpa(mpaDao.getById(f.getMpa().getId()));
            for (Genre g : filmStorage.getGenres(f.getId())) {
                f.getGenres().add(g);
            }
        }

        return films;
    }

    public Film delete(Integer id) {
        log.info("Удаляем фильм с id {}", id);
        return filmStorage.delete(id);
    }

    public Film getById(Integer id) {
        log.info("получить фильм по id {}", id);
        if (!filmStorage.exists(id)) {
            throw new NotFoundException(String.format("Фильма с id = %s не существует...", id));
        }
        Film film = filmStorage.getById(id);
        film.setMpa(mpaDao.getById(film.getMpa().getId()));
        for (Genre g : filmStorage.getGenres(film.getId())) {
            film.getGenres().add(g);
        }
        return film;
    }

    public Film addLike(Integer filmId, Integer userId) {
        log.info("Добавляем лайк пользователя с id {} у фильма id {}.", userId, filmId);
        likeDao.add(filmId, userId);
        return filmStorage.getById(filmId);
    }

    public Film deleteLike(Integer filmId, Integer userId) {
        log.info("Удаляем лайк пользователя с id {} у фильма id {}.", userId, filmId);
        List<Integer> idList = getAll().stream().map(Film::getId).collect(Collectors.toList());
        if (!idList.contains(filmId)) throw new NotFoundException(String.format("Фильм с id = %s не найден", filmId));
        if (!likeDao.isFilmLikedByUser(filmId, userId))
            throw new NotFoundException(String.format("Лайк пользователя с id = %s, у фильма id = %s, не найден", userId, filmId));
        likeDao.remove(filmId, userId);
        return filmStorage.getById(filmId);
    }

    public List<Film> getMostPopular(Integer count) {
        log.info("Запрос популярных фильмов с параметром - колличество {}.", count);
        return getAll().stream().sorted(((o1, o2) -> likeDao.getCount(o2.getId()) - likeDao.getCount(o1.getId()))).limit(count).collect(Collectors.toList());
    }
}
