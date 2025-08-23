package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FindingException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.interfaces.UserStorage;

import java.sql.Timestamp;
import java.util.*;

@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage {

    FriendDbStorage friendDbStorage;

    private static final String insert = "INSERT " +
                                            "INTO users (email, login, name, birthday) " +
                                            "VALUES (?, ?, ?, ?)";

    private static final String update = "UPDATE users " +
                                            "SET email = ?, login = ?, " +
                                            "name = ?, birthday = ? " +
                                            "WHERE id = ?";

    private static final String findAll = "SELECT * " +
                                            "FROM users";

    private static final String findById = "SELECT * " +
                                                "FROM users " +
                                                "WHERE id = ?";

    private static final String delete = "DELETE " +
                                            "FROM users " +
                                            "WHERE id = ?";

    private static final String findMany = "SELECT * " +
                                                "FROM users " +
                                                "WHERE id IN (%s)";

    private static final String findCommonFriends = "SELECT id " +
                                                        "FROM users " +
                                                        "WHERE id IN " +
                                                            "(SELECT friend_id " +
                                                            "FROM user_friends " +
                                                            "WHERE user_id = ?) AND id IN " +
                                                            "(SELECT friend_id " +
                                                            "FROM user_friends " +
                                                            "WHERE user_id = ?);";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper, FriendDbStorage friendDbStorage) {
        super(jdbc, mapper, User.class);
        this.friendDbStorage = friendDbStorage;
    }

    @Override
    public User createUser(User user) {
        int id = insert(
                insert,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Timestamp.valueOf(user.getBirthday().atStartOfDay())
        );
        user.setId(id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        try {
            update(
                    update,
                    user.getEmail(),
                    user.getLogin(),
                    user.getName(),
                    Timestamp.valueOf(user.getBirthday().atStartOfDay()),
                    user.getId()
            );
            return getUserById(user.getId());
        } catch (InternalServerException e) {
            throw new FindingException(e.getMessage());
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        Collection<User> users = findMany(findAll);
        Map<Integer, Set<Integer>> friends = friendDbStorage.getAllFriendsId();
        for (User user : users) {
            user.setFriends(friends.getOrDefault(user.getId(), new HashSet<>()));
        }
        return users;
    }

    @Override
    public User getUserById(int id) {
        User user = findOne(findById, id)
                .orElseThrow(() -> new FindingException("Пользователь с id = " + id + " не найден"));
        user.setFriends(friendDbStorage.getFriendsId(user.getId()));
        return user;
    }

    @Override
    public User deleteUser(int id) {
        User user = getUserById(id);
        if (!user.getFriends().isEmpty()) friendDbStorage.deleteAllUserFriends(id);
        update(delete, id);
        return user;
    }

    public Collection<User> getFriends(int id) {
        getUserById(id);
        Collection<Integer> friends = friendDbStorage.getFriendsId(id);
        return findMany(friends);
    }

    private Collection<User> findMany(Collection<Integer> usersId) {
        if (usersId.isEmpty()) return new ArrayList<>();
        String inSql = String.join(",", Collections.nCopies(usersId.size(), "?"));

        List<User> result = jdbc.query(
                String.format(findMany, inSql),
                usersId.toArray(),
                rowMapper);
        if (result.size() != usersId.size()) throw new ValidationException("Указан User с неверным id!");
        return result;
    }

    public Collection<User> getCommonFriends(int firstUserId, int secondUserId) {
        Collection<Integer> friends = jdbc.queryForList(findCommonFriends, Integer.TYPE, firstUserId, secondUserId);
        return findMany(friends);
    }
}
