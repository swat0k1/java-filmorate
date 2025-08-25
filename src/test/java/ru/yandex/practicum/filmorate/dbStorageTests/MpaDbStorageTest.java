package ru.yandex.practicum.filmorate.dbStorageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dbStorage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class, MpaRowMapper.class})
public class MpaDbStorageTest {

    private final MpaDbStorage mpaDbStorage;

    @Test
    void shouldFindMpaById() {
        Mpa mpa = mpaDbStorage.findById(1).orElseThrow(() -> new AssertionError("Рейтинг не найден"));
        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void shouldFindAllMpas() {
        assertThat(!mpaDbStorage.findAll().isEmpty());
    }

    @Test
    void shouldReturnEmptyForNonExistingId() {
        assertThat(mpaDbStorage.findById(999)).isEmpty();
    }

    @Test
    void shouldReturnUniqueMpaIds() {

        var allMpas = mpaDbStorage.findAll();
        var uniqueIds = allMpas.stream().map(Mpa::getId).distinct().count();
        assertThat(uniqueIds).isEqualTo(allMpas.size());
    }
}
