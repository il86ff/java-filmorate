package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ValidFilmReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film {
    private final Set<Integer> likesUserId;
    private Integer id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @ValidFilmReleaseDate
    private LocalDate releaseDate;
    @Positive
    private long duration;

    public Film() {
        likesUserId = new HashSet<>();
    }
}
