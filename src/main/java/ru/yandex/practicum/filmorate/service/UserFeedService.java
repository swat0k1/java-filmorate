package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.storage.dbStorage.UserFeedDbStorage;

import java.util.Collection;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserFeedService {
    private final UserFeedDbStorage userFeedDbStorage;

    public Collection<UserFeed> getUserFeed(int id) {
        return userFeedDbStorage.getUserFeed(id);
    }

}
