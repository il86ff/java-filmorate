package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class MpaDaoImpl implements MpaDao {
    private final JdbcTemplate jdbcTemplate;

    public Mpa getById(Integer id) {
        Mpa mpa = jdbcTemplate.queryForObject("SELECT mpa_id, mpa_rating FROM film_mpa WHERE mpa_id=?", new MpaMapper(), id);
        return mpa;
    }

    public Collection<Mpa> getAll() {
        Collection<Mpa> mpaCollection = jdbcTemplate.query("SELECT mpa_id, mpa_rating FROM film_mpa ORDER BY mpa_id", new MpaMapper());
        return mpaCollection;
    }

    @Override
    public boolean exist(Integer id) {
        try {
            getById(id);
            return true;
        } catch (EmptyResultDataAccessException exception) {
            return false;
        }
    }
}
