package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private final ArrayList<User> users = new ArrayList<>();
    private int currentId = 1;

    @Override
    public User createUser(User user) {
        validateUser(user);
        if ((user.getName() == null || user.getName().isEmpty()) && !user.getLogin().isEmpty()) {
            user.setName(user.getLogin());
        }
        user.setId(currentId);
        currentId++;
        users.add(user);
        log.info("Пользователь создан: {}", user);
        return user;
    }

    @Override
    public User updateUser(int id, User updatedUser) {
        validateUser(updatedUser);
        Optional<User> existUser = users.stream().filter(user -> user.getId() == id).findFirst();

        if (existUser.isPresent()) {
            User user = existUser.get();
            user.setEmail(updatedUser.getEmail());
            user.setLogin(updatedUser.getLogin());
            user.setName(updatedUser.getName());
            user.setBirthday(updatedUser.getBirthday());
            log.info("Пользователь обновлен: {}", user);
            return user;
        } else {
            log.warn("Пользователь с указанным id {} не был найден", id);
            throw new ValidationException("{\n" +
                    " \"error\": \"Пользователь с таким id не найден.\"\n" +
                    "}");
        }
    }

    @Override
    public ArrayList<User> getAllUsers() {
        return users;
    }

    @Override
    public User getUserById(int id) {
        return users.stream().filter(user -> user.getId() == id).findFirst().orElse(null);
    }

    @Override
    public void deleteUser(int id) {
        users.removeIf(user -> user.getId() == id);
    }

    private void validateUser(User user) {

        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Ошибка валидации пользователя: электронная почта не может быть пустой и должна содержать символ '@'.");
            throw new ValidationException("{\n" +
                    "    \"error\": \"Электронная почта не может быть пустой и должна содержать символ '@'!\"\n" +
                    "}");
        }

        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Ошибка валидации пользователя: логин не может быть пустым и содержать пробелы.");
            throw new ValidationException("{\n" +
                    "    \"error\": \"Логин не может быть пустым и содержать пробелы!\"\n" +
                    "}");
        }

        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Ошибка валидации пользователя: дата рождения не может быть больше текущей даты или пустой.");
            throw new ValidationException("{\n" +
                    "    \"error\": \"Дата рождения не может быть больше текущей даты или пустой!\"\n" +
                    "}");
        }

    }

}
