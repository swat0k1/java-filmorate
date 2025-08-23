package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dbStorage.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private FriendDbStorage friendDbStorage;

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User updatedUser) {
        return userStorage.updateUser(updatedUser);
    }

    public Collection<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addFriend(int userId, int friendId) {

        if (userId == friendId) {
            throw new ValidationException("Пользователь не может добавить сам себя в друзья!");
        }

        friendDbStorage.addFriend(userId, friendId);
        return userStorage.getUserById(userId);

    }

    public User removeFriend(int userId, int friendId) {

        if (userId == friendId) {
            throw new ValidationException("id пользователей не должны совпадать!");
        }

        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        if (user.getFriends().contains(friend.getId())) {
            friendDbStorage.deleteFriend(userId, friendId);
        }
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

}
