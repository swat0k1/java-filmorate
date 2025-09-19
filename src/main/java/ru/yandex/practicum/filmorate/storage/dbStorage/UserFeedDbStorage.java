package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.UserFeed;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserFeedDbStorage extends BaseRepository<UserFeed> {
    private static final String GET_ALL_EVENTS_FOR_USER = """
            SELECT user_id, timestamp, event_type, operation, event_id, entity_id
            FROM user_feed
            WHERE user_id = ?
               OR user_id IN (SELECT friend_id FROM user_friends WHERE user_id = ?)
            """;

    public UserFeedDbStorage(JdbcTemplate jdbc, RowMapper<UserFeed> mapper) {
        super(jdbc, mapper, UserFeed.class);
    }

    public UserFeed save(UserFeed feedEvent) {
        SimpleJdbcInsert inserter = new SimpleJdbcInsert(jdbc)
                .withTableName("user_feed")
                .usingGeneratedKeyColumns("event_id");

        Map<String, Object> params = Map.of(
                "user_id", feedEvent.getUserId(),
                "event_type", feedEvent.getEventType().name(),
                "operation", feedEvent.getOperation().name(),
                "entity_id", feedEvent.getEntityId(),
                "timestamp", Timestamp.from(
                        feedEvent.getTimestamp() != null ? feedEvent.getTimestamp() : java.time.Instant.now()
                )
        );

        Number key = inserter.executeAndReturnKey(params);
        feedEvent.setEventId(key.intValue());
        return feedEvent;
    }

    public List<UserFeed> getUserFeed(int userId) {
        try {
            return findMany(GET_ALL_EVENTS_FOR_USER, userId, userId);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка получения ленты.");
        }
    }
}
