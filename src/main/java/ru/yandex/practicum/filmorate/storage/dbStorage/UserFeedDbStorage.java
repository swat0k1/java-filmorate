package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.UserFeed;

import java.util.List;

@Repository
@Slf4j
public class UserFeedDbStorage extends BaseRepository<UserFeed> {
    private static final String INSERT_EVENT = """
            INSERT INTO user_feed (timestamp, user_id, event_type, operation, entity_id)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String GET_ALL_EVENTS_FOR_USER = """
            SELECT user_id, timestamp, event_type, operation, event_id, entity_id
            FROM user_feed
            WHERE user_id = ?
            """;

    public UserFeedDbStorage(JdbcTemplate jdbc, RowMapper<UserFeed> mapper) {
        super(jdbc, mapper, UserFeed.class);
    }

    public UserFeed save(UserFeed feedEvent) {
        var ts = feedEvent.getTimestamp() != null ? feedEvent.getTimestamp() : java.time.Instant.now();

        int generatedId = insert(
                INSERT_EVENT,
                java.sql.Timestamp.from(ts),
                feedEvent.getUserId(),
                feedEvent.getEventType().name(),
                feedEvent.getOperation().name(),
                feedEvent.getEntityId()
        );

        feedEvent.setEventId(generatedId);
        feedEvent.setTimestamp(ts);
        System.out.println(feedEvent);
        return feedEvent;
    }

    public List<UserFeed> getUserFeed(int userId) {
        try {
            return findMany(GET_ALL_EVENTS_FOR_USER, userId);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка получения ленты.");
        }
    }
}
