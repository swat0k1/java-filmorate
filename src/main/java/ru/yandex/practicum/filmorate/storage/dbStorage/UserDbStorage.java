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

    private FriendDbStorage friendDbStorage;

    private static final String INSERT = "INSERT " +
            "INTO users (email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";

    private static final String UPDATE = "UPDATE users " +
            "SET email = ?, login = ?, " +
            "name = ?, birthday = ? " +
            "WHERE id = ?";

    private static final String FIND_ALL = "SELECT * " +
            "FROM users";

    private static final String FIND_BY_ID = "SELECT * " +
            "FROM users " +
            "WHERE id = ?";

    private static final String DELETE = "DELETE " +
            "FROM users " +
            "WHERE id = ?";

    private static final String FIND_MANY = "SELECT * " +
            "FROM users " +
            "WHERE id IN (%s)";

    private static final String FIND_COMMON_FRIENDS = "SELECT id " +
            "FROM users " +
            "WHERE id IN " +
            "(SELECT friend_id " +
            "FROM user_friends " +
            "WHERE user_id = ?) AND id IN " +
            "(SELECT friend_id " +
            "FROM user_friends " +
            "WHERE user_id = ?);";

    private static final String FIND_ALL_USER_LIKES = "SELECT * " +
            "FROM film_likes";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper, FriendDbStorage friendDbStorage) {
        super(jdbc, mapper, User.class);
        this.friendDbStorage = friendDbStorage;
    }

    @Override
    public User createUser(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        int id = insert(
                INSERT,
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
                    UPDATE,
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
        Collection<User> users = findMany(FIND_ALL);
        Map<Integer, Set<Integer>> friends = friendDbStorage.getAllFriendsId();
        for (User user : users) {
            user.setFriends(friends.getOrDefault(user.getId(), new HashSet<>()));
        }
        return users;
    }

    @Override
    public User getUserById(int id) {
        User user = findOne(FIND_BY_ID, id)
                .orElseThrow(() -> new FindingException("Пользователь с id = " + id + " не найден"));
        user.setFriends(friendDbStorage.getFriendsId(user.getId()));
        return user;
    }

    @Override
    public User deleteUser(int id) {
        User user = getUserById(id);
        delete(DELETE, id);
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
                String.format(FIND_MANY, inSql),
                usersId.toArray(),
                rowMapper);
        if (result.size() != usersId.size()) throw new ValidationException("Указан User с неверным id!");
        return result;
    }

    public Collection<User> getCommonFriends(int firstUserId, int secondUserId) {
        Collection<Integer> friends = jdbc.queryForList(FIND_COMMON_FRIENDS, Integer.TYPE, firstUserId, secondUserId);
        return findMany(friends);
    }

    public Set<Integer> getRecommendations(int id) {
        // Карта, в которой ключ = id пользователя, а значение = множество id фильмов, которые он лайкнул
        HashMap<Integer, Set<Integer>> allUsersLike = new HashMap<>();
        jdbc.query(FIND_ALL_USER_LIKES, rs -> {
            // Возврат id пользователя
            int userId = rs.getInt("user_liked_id");
            // Заполнение карты данными из БД
            Set<Integer> usersLikes = allUsersLike.computeIfAbsent(userId, key -> new HashSet<>());
            usersLikes.add(rs.getInt("film_id"));
        });

        // Множество id фильмов, которые лайкнул пользователь
        Set<Integer> usersLike = allUsersLike.get(id);
        if (usersLike == null) {
            return new HashSet<>();
        }

        // Находим множество id фильмов другого пользователя с максимальным количеством совпадений
        Set<Integer> maxMatchLike = null;
        int maxMatches = 0;

        for (Map.Entry<Integer, Set<Integer>> entry : allUsersLike.entrySet()) {
            Set<Integer> currentSetLike = entry.getValue();
            // Исключаем сравнение с самим собой,
            // либо с пользователем с абсолютно одинаковыми лайками, так как рекомендовать тогда нечего
            if (!currentSetLike.equals(usersLike)) {
                int matches = (int) currentSetLike.stream()
                        .filter(usersLike::contains)
                        .count();
                if (matches > maxMatches) {
                    maxMatches = matches;
                    maxMatchLike = currentSetLike;
                }
            }
        }
        if (maxMatches == 0) {
            return new HashSet<>();
        }

        // Исключаем из найденного множества элементы из usersLike
        if (maxMatchLike != null) {
            maxMatchLike.removeAll(usersLike);
        }
        if (maxMatches == 0) {
            return new HashSet<>();
        }
        return maxMatchLike;
    }
}
