package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.DuplicateIdException;
import ru.yandex.practicum.filmorate.exception.IdIsNullException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User add(User user) {
        log.info("Добавляем пользователя {}", user);
        if (userStorage.getAll().contains(userStorage.getById(user.getId()))) {
            log.info("Пользователь с id = {} уже существует...", user.getId());
            throw new DuplicateIdException(String.format("Пользователь с id = %s уже существует...", user.getId()));
        }
        user.setName(user.getName().isBlank() ? user.getLogin() : user.getName());
        return userStorage.add(user);
    }

    public User update(User user) {
        log.info("Добавляем пользователя {}", user);
        if (user.getId() == null) {
            log.warn("id пользователя забыли передать...");
            throw new IdIsNullException("Ошибка обновления пользователя... ID не передан");
        }
        if (!userStorage.getAll().contains(userStorage.getById(user.getId()))) {
            log.warn("Пользователь с id = {} не существует...", user.getId());
            throw new NotFoundException("Пользователь не существует...");
        }
        return userStorage.update(user);
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User delete(Integer id) {
        log.info("Удаляем пользователя с id {}", id);
        return userStorage.delete(id);
    }

    public User getById(Integer id) {
        if (userStorage.getById(id) == null) {
            throw new NotFoundException(String.format("Пользователя с id = %s не существует...", id));
        }
        return userStorage.getById(id);
    }

    public User addFriend(Integer id, Integer friendId) {
        log.info("Добавляем пользователю с id {}, друга с id {}", id, friendId);
        List<Integer> idList = getAll().stream().map(User::getId).collect(Collectors.toList());
        if (!idList.contains(id)) throw new NotFoundException(String.format("Пользователь с id = %s не найден", id));
        if (!idList.contains(friendId))
            throw new NotFoundException(String.format("Пользователь с id = %s не найден", friendId));
        userStorage.addFriend(id, friendId);
        return getById(id);
    }

    public User removeFriend(Integer id, Integer friendId) {
        log.info("Удаляем у пользователя с id {}, друга с id {}", id, friendId);
        userStorage.removeFriend(id, friendId);
        return getById(id);
    }

    public List<User> getFriends(Integer id) {
        log.info("Вывести друзей пользователя с id {}", id);
        return userStorage.getById(id).getFriendsId().stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer id, Integer friendId) {
        log.info("Вывести общих друзей пользователя с id {} и пользователья с id {}", id, friendId);
        List<Integer> commonId = new ArrayList<>();
        for (Integer i : getById(id).getFriendsId()) {
            if (getById(friendId).getFriendsId().contains(i)) {
                commonId.add(i);
            }
        }
        return commonId.stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }
}
