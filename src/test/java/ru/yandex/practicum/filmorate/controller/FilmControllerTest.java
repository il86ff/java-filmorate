package ru.yandex.practicum.filmorate.controller;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
@AutoConfigureMockMvc
public class FilmControllerTest {
    @Autowired
    FilmController filmController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenPostValidFilmThenCorrectResponse() throws Exception {
        String film = "{\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"Film name\",\n" +
                "    \"description\": \"Пятеро друзей\",\n" +
                "    \"releaseDate\": \"1900-03-25\",\n" +
                "    \"duration\": 200\n" +
                "}";

        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .content(film)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void whenPostFilmWithOutNameThenBadRequest() throws Exception {
        String film = "{\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"\",\n" +
                "    \"description\": \"Пятеро друзей\",\n" +
                "    \"releaseDate\": \"1900-03-25\",\n" +
                "    \"duration\": 200\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .content(film)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", Is.is("must not be blank")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void whenPostFilmWithReleaseDateOutOfBoundsThenBadRequest() throws Exception {
        String film = "{\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"Film name\",\n" +
                "    \"description\": \"Пятеро друзей\",\n" +
                "    \"releaseDate\": \"1700-03-25\",\n" +
                "    \"duration\": 200\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                        .content(film)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.releaseDate", Is.is("Release date must be after 28.12.1895")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void whenPostFilmWithTooLongDescriptionThenBadRequest() throws Exception {
        String film = "{\n" +
                "    \"id\": 2,\n" +
                "    \"name\": \"Film name\",\n" +
                "    \"description\": \"dddddddddddddddddddddddddddddddddddddddeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeesssssssssssssssssssssssssssssssssssssccccccccccccccccccccccccccccccccccccccccptonnnnnnnnnnnnnnnasd\",\n" +
                "    \"releaseDate\": \"1900-03-25\",\n" +
                "    \"duration\": 200\n" +
                "}";
        mockMvc.perform(MockMvcRequestBuilders.post("/films")
                .content(film)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", Is.is("size must be between 0 and 200")))
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));
    }
}
