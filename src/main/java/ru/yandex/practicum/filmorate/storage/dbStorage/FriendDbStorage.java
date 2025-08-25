package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FindingException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.util.*;

@Repository
public class FriendDbStorage extends BaseRepository<User> {

    private static final String ADD = "INSERT " +
                                        "INTO user_friends (user_id, friend_id) " +
                                        "VALUES (?, ?)";

    private static final String FIND_FRIENDS_BY_ID = "SELECT friend_id " +
                                                    "FROM user_friends " +
                                                    "WHERE user_id = ?";

    private static final String FIND_ALL_FRIENDS = "SELECT * " +
                                                    "FROM user_friends";

    private static final String DELETE = "DELETE " +
                                            "FROM user_friends " +
                                            "WHERE user_id = ? AND friend_id = ?";

    private static final String DELETE_ALL_USER_FRIENDS = "DELETE " +
                                                        "FROM user_friends " +
                                                        "WHERE user_id = ? " +
                                                        "OR friend_id = ?";

    public FriendDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }

    public void addFriend(int userId, int friendId) {
        try {
            update(ADD, userId, friendId);
        } catch (DataIntegrityViolationException e) {
            throw new FindingException(e.getMessage());
        }
    }

    public Set<Integer> getFriendsId(int userId) {
        List<Integer> friends = jdbc.queryForList(FIND_FRIENDS_BY_ID, Integer.TYPE, userId);
        return new HashSet<>(friends);
    }

    public Map<Integer, Set<Integer>> getAllFriendsId() {
        Map<Integer, Set<Integer>> friends = new HashMap<>();
        return jdbc.query(FIND_ALL_FRIENDS, (ResultSet resultSet) -> {
            while (resultSet.next()) {
                Integer userId = resultSet.getInt("user_id");
                Integer friendId = resultSet.getInt("friend_id");
                friends.computeIfAbsent(userId, k -> new HashSet<>()).add(friendId);
            }
            return friends;
        });
    }

    public void deleteFriend(int userId, int friendId) {
        try {
            update(DELETE, userId, friendId);
        } catch (InternalServerException e) {
            throw new FindingException(e.getMessage());
        }
    }

    public void deleteAllUserFriends(int userId) {
        update(DELETE_ALL_USER_FRIENDS, userId, userId);
    }
}
