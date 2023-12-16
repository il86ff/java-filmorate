package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    @Positive
    private Integer id;
    @Positive
    private String name;

    public Genre(Integer id) {
        this.id = id;
    }
}
