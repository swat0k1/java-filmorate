package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaDbStorage extends BaseRepository<Mpa> {

    private static final String findById = "SELECT * " +
                                                "FROM rating_MPA " +
                                                "WHERE mpa_id = ?";
    private static final String findAll = "SELECT * " +
                                            "FROM rating_MPA";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper, Mpa.class);
    }

    public Optional<Mpa> findById(int mpaId) {
        return findOne(findById, mpaId);
    }

    public List<Mpa> findAll() {
        return findMany(findAll);
    }
}
