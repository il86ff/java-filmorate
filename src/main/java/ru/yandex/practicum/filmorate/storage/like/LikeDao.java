package ru.yandex.practicum.filmorate.storage.like;

public interface LikeDao {
    void add(Integer filmId, Integer userId);

    void remove(Integer filmId, Integer userId);

    int getCount(Integer filmId);

    boolean isFilmLikedByUser(Integer filmId, Integer userId);

}
