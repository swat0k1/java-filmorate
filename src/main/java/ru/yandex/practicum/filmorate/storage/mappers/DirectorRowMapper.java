package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class DirectorRowMapper implements RowMapper<Director> {

    public Director mapRow(ResultSet rs, int rowNum) {
        try {
            return Director.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("director_name"))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
