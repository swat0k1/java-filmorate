package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;

public interface UserStorage {

    User createUser(User user);

    User updateUser(int id, User user);

    ArrayList<User> getAllUsers();

    User getUserById(int id);

    void deleteUser(int id);

}
