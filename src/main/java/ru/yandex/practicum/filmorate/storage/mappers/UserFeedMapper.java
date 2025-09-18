package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserFeedMapper implements RowMapper<UserFeed> {
    @Override
    public UserFeed mapRow(ResultSet rs, int rowNum) {
        try {
            var ts = rs.getTimestamp("timestamp");
            UserFeed feed = new UserFeed();
            feed.setEventId(rs.getInt("event_id"));
            feed.setTimestamp(ts != null ? ts.toInstant() : null);
            feed.setUserId(rs.getInt("user_id"));
            feed.setEventType(EventType.valueOf(rs.getString("event_type")));
            feed.setOperation(Operation.valueOf(rs.getString("operation")));
            feed.setEntityId(rs.getInt("entity_id"));
            return feed;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
