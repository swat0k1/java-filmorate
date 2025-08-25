package ru.yandex.practicum.filmorate.dbStorageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dbStorage.*;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import({FilmDbStorage.class,
        FilmRowMapper.class,
        MpaDbStorage.class,
        MpaRowMapper.class,
        LikeDbStorage.class,
        GenreDbStorage.class,
        GenreRowMapper.class,
        UserDbStorage.class,
        UserRowMapper.class,
        FriendDbStorage.class})
@JdbcTest

public class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;
    private final LikeDbStorage likeDbStorage;
    private final UserDbStorage userDbStorage;
    private final GenreDbStorage genreDbStorage;

    private Film film1;
    private Film film2;
    private User user1;

    @BeforeEach
    void beforeALL() {

        film1 = new Film();
        film1.setId(1);
        film1.setName("TestName");
        film1.setDescription("TestDescription");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        film1.setMpa(new Mpa(1, null));

        Set<Genre> genres = new HashSet<>();
        genres.add(new Genre(1, "test"));
        film1.setGenres(genres);

        film2 = new Film();
        film2.setId(2);
        film2.setName("TestName2");
        film2.setDescription("TestDescription2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(121);
        film2.setMpa(new Mpa(2, null));

        user1 = new User();
        user1.setId(1);
        user1.setEmail("test@test.ru");
        user1.setName("test");
        user1.setLogin("test");
        user1.setBirthday(LocalDate.of(2000, 1, 1));

    }

    @Test
    void createAndFindById() {
        filmDbStorage.addFilm(film1);
        assertThat(filmDbStorage.getFilmById(1)).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void findAll() {
        filmDbStorage.addFilm(film1);
        filmDbStorage.addFilm(film2);
        assertThat(filmDbStorage.getAllFilms()).hasSize(2);
    }

    @Test
    void update() {
        filmDbStorage.addFilm(film1);
        film1.setName("TestName3");
        filmDbStorage.updateFilm(film1);
        assertThat(filmDbStorage.getFilmById(1)).hasFieldOrPropertyWithValue("name", "TestName3");
    }

    @Test
    void delete() {
        filmDbStorage.addFilm(film1);
        filmDbStorage.deleteFilm(1);
        assertThat(filmDbStorage.getAllFilms()).isEmpty();
    }

    @Test
    void getPopularFilms() {
        filmDbStorage.addFilm(film1);
        filmDbStorage.addFilm(film2);
        userDbStorage.createUser(user1);
        likeDbStorage.addLike(1, 1);

        Film film = filmDbStorage.getTopFilms(1).iterator().next();
        assertThat(film).hasFieldOrPropertyWithValue("name", "TestName");
    }
}
