package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) {
        try {
            return new Film(rs.getInt("id"), rs.getString("name"),
                                rs.getString("description"),
                                rs.getTimestamp("release_date").toLocalDateTime().toLocalDate(),
                                rs.getInt("duration"), new Mpa(rs.getInt("mpa_id"),
                                rs.getString("mpa_name")));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

}
