package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GenreServiceTest {
    private final GenreService genreService;
    private final Genre comedy = new Genre(1, "Комедия");
    private final Genre drama = new Genre(2, "Драма");
    private final Genre cartoon = new Genre(3, "Мультфильм");

    @Test
    public void shouldCorrectlyReturnMpaById() {
        Assertions.assertEquals(genreService.getById(1), comedy);
        Assertions.assertEquals(genreService.getById(2), drama);
        Assertions.assertEquals(genreService.getById(3), cartoon);
    }

    @Test
    public void shouldNotReturnMpaWithIncorrectId() {
        Assertions.assertThrows(NotFoundException.class, () -> genreService.getById(-1));
    }

    @Test
    public void shouldReturnListOfGenres() {
        Assertions.assertTrue(genreService.getAll().contains(drama));
        Assertions.assertTrue(genreService.getAll().contains(comedy));
        Assertions.assertTrue(genreService.getAll().contains(cartoon));
    }
}
