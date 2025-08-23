package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {

        return new User(rs.getInt("id"), rs.getString("email"),
                                rs.getString("login"), rs.getString("name"),
                                rs.getTimestamp("birthday").toLocalDateTime().toLocalDate());
    }

}
