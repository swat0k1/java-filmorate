package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRowMapper implements RowMapper<Mpa> {

    @Override
    public Mpa mapRow(ResultSet rs, int rowNum) {
        try {
            return new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
