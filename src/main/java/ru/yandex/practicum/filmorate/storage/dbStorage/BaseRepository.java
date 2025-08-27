package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.FindingException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseRepository<T> {

    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> rowMapper;
    private final Class<T> entityType;

    protected int insert(String query, Object... params) {

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);

            }
            return ps;
        }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            if (id > Integer.MAX_VALUE || id < Integer.MIN_VALUE) {
                throw new InternalServerException("Сгенерированный ключ за пределами диапазона Integer");
            }
            return id.intValue();
        } else {
            throw new InternalServerException("Ошибка сохранения данных");
        }
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Ошибка обновления данных");
        }
    }

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, rowMapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, rowMapper, params);
    }

    protected void batchUpdateBase(String query, BatchPreparedStatementSetter bps) {
        int[] rowsUpdated = jdbc.batchUpdate(query, bps);
        if (rowsUpdated.length == 0) {
            throw new FindingException("Ошибка обновления данных");
        }
    }
}
