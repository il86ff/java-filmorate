package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.Collection;

@Slf4j
@Component("DBUserStorage")
@RequiredArgsConstructor
public class DBUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User add(User user) {
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) "
                        + "VALUES (?, ?, ?, ?)",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()));
        User userFromDB = jdbcTemplate.queryForObject(
                "SELECT user_id, email, login, name, birthday "
                        + "FROM users "
                        + "WHERE email=?", new UserMapper(), user.getEmail());
        return userFromDB;
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update("UPDATE users "
                        + "SET email=?, login=?, name=?, birthday=? "
                        + "WHERE user_id=?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        User userFromDB = getById(user.getId());
        return userFromDB;
    }

    @Override
    public User delete(Integer id) {
        return null;
    }

    @Override
    public Collection<User> getAll() {
        Collection<User> users = jdbcTemplate.query(
                "SELECT user_id, email, login, name, birthday FROM users",
                new UserMapper());
        return users;
    }

    @Override
    public User getById(Integer id) {
        User userFromDB = jdbcTemplate.queryForObject(
                "SELECT user_id, email, login, name, birthday FROM users "
                        + "WHERE user_id=?", new UserMapper(), id);
        return userFromDB;
    }

    @Override
    public User addFriend(Integer id, Integer friendId) {
        return null;
    }

    @Override
    public User removeFriend(Integer id, Integer friendId) {
        return null;
    }

    @Override
    public boolean exists(Integer id) {
        try {
            getById(id);
            return true;
        } catch (EmptyResultDataAccessException e) {
            log.trace("there is no data for user with id = {}", id);
            return false;
        }
    }
}
