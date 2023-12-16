package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.DuplicateIdException;
import ru.yandex.practicum.filmorate.exception.IdIsNullException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipDao;
import ru.yandex.practicum.filmorate.storage.user.DBUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final UserStorage userStorage;
    private final FriendshipDao friendshipDao;

    @Autowired
    public UserService(@Qualifier("DBUserStorage") DBUserStorage userStorage, FriendshipDao friendshipDao) {
        this.userStorage = userStorage;
        this.friendshipDao = friendshipDao;
    }


    public User add(User user) {
        log.info("Добавляем пользователя {}", user);
        if (user.getId() != null) {
            if (userStorage.exists(user.getId())) {
                throw new DuplicateIdException(String.format("Пользователь с id = %s уже существует...", user.getId()));
            } else {
                throw new IllegalArgumentException("ID присваевается в БД...");
            }
        }
        user.setName(user.getName().isBlank() ? user.getLogin() : user.getName());
        return userStorage.add(user);
    }

    public User update(User user) {
        log.info("Добавляем пользователя {}", user);
        if (user.getId() == null) {
            throw new IdIsNullException(String.format("Ошибка обновления пользователя %s ID не передан", user));
        }
        if (!userStorage.exists(user.getId())) {
            throw new NotFoundException(String.format("Пользователь с id = %s не существует...", user.getId()));
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
        if (!userStorage.exists(id)) {
            throw new NotFoundException(String.format("Пользователя с id = %s не существует...", id));
        }
        return userStorage.getById(id);
    }

    public User addFriend(Integer id, Integer friendId) {
        log.info("Добавляем пользователю с id {}, друга с id {}", id, friendId);
        List<Integer> idList = getAll().stream().map(User::getId).collect(Collectors.toList());
        if (Objects.equals(id, friendId)) throw new DuplicateIdException("нельзя добавить себя в друзья...");
        if (!idList.contains(id)) throw new NotFoundException(String.format("Пользователь с id = %s не найден", id));
        if (!idList.contains(friendId))
            throw new NotFoundException(String.format("Пользователь с id = %s не найден", friendId));
        if (!friendshipDao.exist(id, friendId)) friendshipDao.addFriend(id, friendId, true);
        return getById(id);
    }

    public User removeFriend(Integer id, Integer friendId) {
        log.info("Удаляем у пользователя с id {}, друга с id {}", id, friendId);
        friendshipDao.deleteFriend(id, friendId);
        return getById(id);
    }

    public List<User> getFriends(Integer id) {
        log.info("Вывести друзей пользователя с id {}", id);
        return friendshipDao.getFriends(id).stream().map(this::getById).collect(Collectors.toList());

    }

    public List<User> getCommonFriends(Integer id, Integer friendId) {
        log.info("Вывести общих друзей пользователя с id {} и пользователья с id {}", id, friendId);
        List<Integer> commonId = new ArrayList<>();
        for (Integer i : friendshipDao.getFriends(id)) {
            if (friendshipDao.getFriends(friendId).contains(i)) {
                commonId.add(i);
            }
        }
        return commonId.stream().map(this::getById).collect(Collectors.toList());
    }
}
