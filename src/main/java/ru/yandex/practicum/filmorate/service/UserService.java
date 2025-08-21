package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.FriendshipStatus;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(int id, User updatedUser) {
        return userStorage.updateUser(id, updatedUser);
    }

    public ArrayList<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (user == null || friend == null) {
            throw new ValidationException("{\n" +
                    "    \"error\": \"Пользователь не найден!\"\n" +
                    "}");
        }

        user.getFriends().put(friendId, FriendshipStatus.PENDING);
        friend.getFriends().put(userId, FriendshipStatus.PENDING);

        log.info("Пользователь {} добавил в друзья {}", userId, friendId);
    }

    public void confirmFriendship(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (user == null || friend == null) {
            throw new ValidationException("{\n" +
                    "    \"error\": \"Пользователь не найден!\"\n" +
                    "}");
        }

        if (user.getFriends().containsKey(friendId) &&
                user.getFriends().get(friendId) == FriendshipStatus.PENDING) {
            user.getFriends().put(friendId, FriendshipStatus.CONFIRMED);
            friend.getFriends().put(userId, FriendshipStatus.CONFIRMED); // Делаем это двусторонним
            log.info("Пользователь {} подтвердил дружбу с {}", userId, friendId);
        } else {
            throw new ValidationException("{\n" +
                    "    \"error\": \"Дружба не может быть подтверждена!\"\n" +
                    "}");
        }
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (user == null || friend == null) {
            throw new ValidationException("{\n" +
                    "    \"error\": \"Пользователь не найден!\"\n" +
                    "}");
        }

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.info("Пользователь {} удалил из друзей {}", userId, friendId);
    }

    public List<User> getCommonFriends(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (user == null || friend == null) {
            throw new ValidationException("{\n" +
                    "    \"error\": \"Пользователь не найден!\"\n" +
                    "}");
        }

        Set<Integer> commonFriends = user.getFriends().keySet().stream()
                .filter(friend.getFriends()::containsKey)
                .collect(Collectors.toSet());

        return commonFriends.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

}
