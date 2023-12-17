package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.DuplicateIdException;
import ru.yandex.practicum.filmorate.exception.IdIsNullException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;

    private final User user =
            new User(null, "mail@mail.ru", "dolore", "Nick Name", LocalDate.of(1946,8,20));

    private final User user2 =
            new User(null, "mail2@mail.ru", "2dolore", "2Nick Name", LocalDate.of(1946,8,20));

    @AfterEach
    void afterEach() {
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM likes");
    }

    @Test
    public void shouldCorrectlyCreateUser() {
        Assertions.assertTrue(userService.getAll().isEmpty());

        User userFromDB = userService.add(user);

        Assertions.assertFalse(userService.getAll().isEmpty());
    }

    @Test
    public void shouldCorrectlyUpdateUser() {
        User userFromDB = userService.add(user);
        userFromDB.setName("new updated name");
        userService.update(userFromDB);

        Assertions.assertEquals(userService.getById(userFromDB.getId()).getName(),"new updated name");
    }

    @Test
    public void shouldNotUpdateUserIdNotExist() {
        Assertions.assertThrows(IdIsNullException.class, () -> userService.update(user));
    }

    @Test
    public void shouldNotReturnUserWithIncorrectId() {
        Assertions.assertThrows(NotFoundException.class, () -> userService.getById(-1));
    }

    @Test
    public void shouldCorrectlyFindById() {
        User userFromDB = userService.add(user);
        Assertions.assertEquals(user.getName(), userService.getById(userFromDB.getId()).getName());
    }

    @Test
    public void shouldCorrectlyAddFriend() {
        User userFromDB = userService.add(user);
        User friendFromDB = userService.add(user2);
        userService.addFriend(userFromDB.getId(), friendFromDB.getId());
        userService.addFriend(friendFromDB.getId(), userFromDB.getId());

        Assertions.assertFalse(userService.getFriends(userFromDB.getId()).isEmpty());
        Assertions.assertFalse(userService.getFriends(friendFromDB.getId()).isEmpty());
    }

    @Test
    public void shouldNotAddYourselfToFriends() {
        User userFromDB = userService.add(user);

        Assertions.assertThrows(DuplicateIdException.class,
                () -> userService.addFriend(userFromDB.getId(), userFromDB.getId()));
    }

    @Test
    public void shouldCorrectlyRemoveFromFriends() {
        User userFromDB = userService.add(user);
        User friendFromDB = userService.add(user2);

        userService.addFriend(userFromDB.getId(), friendFromDB.getId());
        Assertions.assertFalse(userService.getFriends(userFromDB.getId()).isEmpty());

        userService.removeFriend(userFromDB.getId(), friendFromDB.getId());
        Assertions.assertTrue(userService.getFriends(userFromDB.getId()).isEmpty());

    }
}
