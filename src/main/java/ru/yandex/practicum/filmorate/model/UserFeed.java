package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFeed {

    public UserFeed(int userId, EventType eventType, Operation operation, int entityId) {
        this.userId = userId;
        this.eventType = eventType;
        this.operation = operation;
        this.entityId = entityId;
    }

    private int eventId;

    @JsonIgnore
    @NotNull
    private Instant timestamp;

    @NotNull
    private int userId;

    @NotNull
    private EventType eventType;

    @NotNull
    private Operation operation;

    @NotNull
    private int entityId;

    @JsonGetter("timestamp")
    public long getTimestampMillis() {
        return timestamp != null ? timestamp.toEpochMilli() : 0L;
    }
}
