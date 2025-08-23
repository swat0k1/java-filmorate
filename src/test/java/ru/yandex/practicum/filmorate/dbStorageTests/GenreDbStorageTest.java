package ru.yandex.practicum.filmorate.dbStorageTests;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dbStorage.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreDbStorage.class, GenreRowMapper.class})
public class GenreDbStorageTest {

    private final GenreDbStorage genreDbStorage;

    @Test
    void shouldFindGenreById() {
        Genre genre = genreDbStorage.findById(1).orElseThrow(() -> new AssertionError("Жанр не найден"));
        assertThat(genre).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void shouldFindAllGenres() {
        assertThat(genreDbStorage.findAll()).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyForNonExistingId() {
        AssertionsForClassTypes.assertThat(genreDbStorage.findById(999)).isEmpty();
    }

    @Test
    void shouldReturnUniqueMpaIds() {

        var allMpas = genreDbStorage.findAll();
        var uniqueIds = allMpas.stream().map(Genre::getId).distinct().count();
        AssertionsForClassTypes.assertThat(uniqueIds).isEqualTo(allMpas.size());
    }

}
