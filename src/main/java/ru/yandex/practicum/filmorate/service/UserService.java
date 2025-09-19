package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.dbStorage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.UserFeedDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private FriendDbStorage friendDbStorage;
    private FilmDbStorage filmDbStorage;
    private UserFeedDbStorage userFeedDbStorage;

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User updatedUser) {
        return userStorage.updateUser(updatedUser);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @Transactional
    public User addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new ValidationException("Пользователь не может добавить сам себя в друзья!");
        }
        friendDbStorage.addFriend(userId, friendId);
        userFeedDbStorage.save(new UserFeed(userId, EventType.FRIEND,
                Operation.ADD, friendId));
        return userStorage.getUserById(userId);

    }

    @Transactional
    public User removeFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new ValidationException("id пользователей не должны совпадать!");
        }
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user.getFriends().contains(friend.getId())) {
            friendDbStorage.deleteFriend(userId, friendId);
        }
        userFeedDbStorage.save(new UserFeed(userId, EventType.FRIEND,
                Operation.REMOVE, friendId));
        return userStorage.getUserById(userId);

    }

    public Collection<User> getCommonFriends(int userId, int friendId) {
        return userStorage.getCommonFriends(userId, friendId);
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public Collection<User> getFriends(int id) {
        return userStorage.getFriends(id);
    }

    public User delete(int id) {
        return userStorage.deleteUser(id);
    }

    public Collection<Film> getRecommendations(int id) {
        User user = userStorage.getUserById(id);
        if (user == null) log.warn("Пользователь с id = {} не найден", id);
        // Получение коллекции рекомендуемых фильмов по их id
        Set<Integer> maxMatchLike = userStorage.getRecommendations(id);
        if (maxMatchLike == null || maxMatchLike.isEmpty()) return new ArrayList<>();
        Collection<Film> recommendedFilms = new HashSet<>();
        for (int idFilm : maxMatchLike) {
            recommendedFilms.add(filmDbStorage.getFilmById(idFilm));
        }
        return recommendedFilms;
    }
}
