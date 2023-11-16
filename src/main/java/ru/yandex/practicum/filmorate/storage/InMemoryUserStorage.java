package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();
    private int id = 0;

    @Override
    public User add(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(Integer id) {
        return users.remove(id);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User getById(Integer id) {
        return users.get(id);
    }

    @Override
    public User addFriend(Integer id, Integer friendId) {
        getById(id).getFriendsId().add(friendId);
        getById(friendId).getFriendsId().add(id);
        return null;
    }

    @Override
    public User removeFriend(Integer id, Integer friendId) {
        getById(id).getFriendsId().remove(friendId);
        getById(friendId).getFriendsId().remove(id);
        return getById(id);
    }
}
