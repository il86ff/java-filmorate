package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmServiceTest {
    private final FilmService filmService;
    private final UserService userService;
    private final JdbcTemplate jdbcTemplate;

    private final User user =
            new User(null, "mail@mail.ru", "dolore", "Nick Name", LocalDate.of(1946, 8, 20));

    private final User user2 =
            new User(null, "mail2@mail.ru", "2dolore", "2Nick Name", LocalDate.of(1946, 8, 20));

    private final Film film =
            new Film(null, "TERMINATOR", "about end of humanity"
                    , LocalDate.of(1987, 10, 1), 123, new Mpa(1, "G")
                    , new HashSet<>());

    private final Film film2 =
            new Film(null, "TERMINATOR 2", "about end of humanity for second time..."
                    , LocalDate.of(1987, 10, 1), 123, new Mpa(1, "G")
                    , new HashSet<>());

    @AfterEach
    void afterEach() {
        jdbcTemplate.execute("DELETE FROM users");
        jdbcTemplate.execute("DELETE FROM films");
        jdbcTemplate.execute("DELETE FROM likes");
    }

    @Test
    public void shouldCreateFilm() {
        filmService.add(film);
        Assertions.assertFalse(filmService.getAll().isEmpty());
    }

    @Test
    public void whenPostFilmWithTooLongDescriptionValidationException() {
        film.setDescription("dddddddddddddddddddddddddddddddddddddddeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeesssssssssssssssssssssssssssssssssssssccccccccccccccccccccccccccccccccccccccccptonnnnnnnnnnnnnnnasd");
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> filmService.add(film));
    }

    @Test
    public void shouldCorrectlyUpdateFilm() {
        Film filmFromDB = filmService.add(film);
        filmFromDB.setMpa(new Mpa(2, "PG"));
        filmService.update(filmFromDB);

        Assertions.assertEquals(filmService.getById(filmFromDB.getId()).getMpa().getName(),
                filmFromDB.getMpa().getName());
    }

    @Test
    public void shouldCorrectlyGetByID() {
        Film filmFromDB = filmService.add(film);

        Assertions.assertEquals(filmService.getById(filmFromDB.getId()),
                filmFromDB);
    }

    @Test
    public void shouldThrowsNotFoundExceptionWhenFindByIncorrectId() {
        Film filmFromDB = filmService.add(film);
        Assertions.assertThrows(NotFoundException.class, () -> filmService.getById(-1));
    }

    @Test
    public void shouldCorrectlyReturnPopularFilmsList() {
        User userFromDB = userService.add(user);
        Film filmFromDB = filmService.add(film);
        filmService.addLike(filmFromDB.getId(), userFromDB.getId());

        Collection<Film> films = filmService.getMostPopular(1);
        Assertions.assertTrue(films.contains(filmFromDB));
    }

    @Test
    public void shouldCorrectlyAddLike() {
        User userFromDB = userService.add(user);
        Film filmFromDB = filmService.add(film);
        User userFromDB2 = userService.add(user2);
        Film filmFromDB2 = filmService.add(film2);
        filmService.addLike(filmFromDB.getId(), userFromDB.getId());
        filmService.addLike(filmFromDB.getId(), userFromDB2.getId());

        filmService.addLike(filmFromDB2.getId(), userFromDB.getId());

        Assertions.assertEquals(filmFromDB, filmService.getMostPopular(1).get(0));
    }

    @Test
    public void shouldCorrectlyRemoveLike() {
        User userFromDB = userService.add(user);
        Film filmFromDB = filmService.add(film);
        User userFromDB2 = userService.add(user2);
        Film filmFromDB2 = filmService.add(film2);
        filmService.addLike(filmFromDB.getId(), userFromDB.getId());
        filmService.addLike(filmFromDB.getId(), userFromDB2.getId());

        filmService.addLike(filmFromDB2.getId(), userFromDB.getId());

        Assertions.assertEquals(filmFromDB, filmService.getMostPopular(1).get(0));

        filmService.deleteLike(filmFromDB.getId(), userFromDB.getId());
        filmService.deleteLike(filmFromDB.getId(), userFromDB2.getId());
        Assertions.assertEquals(filmFromDB2, filmService.getMostPopular(1).get(0));
    }
}
