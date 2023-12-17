package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class MpaServiceTest {
    private final MpaService mpaService;
    private final Mpa mpaG = new Mpa(1, "G");
    private final Mpa mpaPG = new Mpa(2, "PG");

    @Test
    public void shouldCorrectlyReturnMpaById() {
        Assertions.assertEquals(mpaService.getById(1), mpaG);
    }

    @Test
    public void shouldNotReturnMpaWithIncorrectId() {
        Assertions.assertThrows(NotFoundException.class, () -> mpaService.getById(-1));
    }

    @Test
    public void shouldReturnListOfMpa() {
        Assertions.assertTrue(mpaService.getAll().contains(mpaG));
        Assertions.assertTrue(mpaService.getAll().contains(mpaPG));
    }
}
