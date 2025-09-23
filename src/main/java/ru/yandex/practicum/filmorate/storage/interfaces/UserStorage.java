package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserStorage {

    User createUser(User user);

    User updateUser(User user);

    Collection<User> getAllUsers();

    User getUserById(int id);

    User deleteUser(int id);

    Collection<User> getFriends(int id);

    Collection<User> getCommonFriends(int firstUserId, int secondUserId);

    Set<Integer> getRecommendations(int id);

}
