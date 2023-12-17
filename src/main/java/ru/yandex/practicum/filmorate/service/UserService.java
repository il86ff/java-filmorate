package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
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

@Slf4j
@Service
public class UserService {
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
                log.warn("Ошибка добавления пользователя. Пользователь уже существует в БД.");
                throw new DuplicateIdException(String.format("Пользователь с id = %s уже существует...", user.getId()));
            } else {
                log.warn("невозможно добавить нового пользователя с предзаполненым id ...");
                throw new IllegalArgumentException("ID присваевается в БД...");
            }
        }
        user.setName(user.getName().isBlank() ? user.getLogin() : user.getName());
        return userStorage.add(user);
    }

    public User update(User user) {
        log.info("Добавляем пользователя {}", user);
        if (user.getId() == null) {
            log.warn("Ошибка обновления пользователя. ID не передан.");
            throw new IdIsNullException(String.format("Ошибка обновления пользователя %s ID не передан", user));
        }
        if (!userStorage.exists(user.getId())) {
            log.warn("Ошибка обновления пользователя. ID = {} не существует в БД.", user.getId());
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
        log.info("получение пользователья по id {}", id);
        if (!userStorage.exists(id)) {
            log.warn("Ошибка получения пользователя по ID. ID = {} не существует в БД.", id);
            throw new NotFoundException(String.format("Пользователя с id = %s не существует...", id));
        }
        return userStorage.getById(id);
    }

    public User addFriend(Integer id, Integer friendId) {
        log.info("Добавляем пользователю с id {}, друга с id {}", id, friendId);
        List<Integer> idList = getAll().stream().map(User::getId).collect(Collectors.toList());
        if (Objects.equals(id, friendId)) {
            log.warn("Ошибка добавления друга пользователя. Невозможно добавить самого себя в друзья...");
            throw new DuplicateIdException("нельзя добавить себя в друзья...");
        }
        if (!idList.contains(id)) {
            log.warn("Ошибка добавления друга пользователя. Пользователя с ID = {} не существует в БД.", id);
            throw new NotFoundException(String.format("Пользователь с id = %s не найден", id));
        }
        if (!idList.contains(friendId)) {
            log.warn("Ошибка добавления друга пользователя. Друга с ID = {} не существует в БД.", friendId);
            throw new NotFoundException(String.format("Пользователь с id = %s не найден", friendId));
        }
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
