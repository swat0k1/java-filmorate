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

    private static final String add = "INSERT " +
                                        "INTO user_friends (user_id, friend_id) " +
                                        "VALUES (?, ?)";

    private static final String findFriendsById = "SELECT friend_id " +
                                                    "FROM user_friends " +
                                                    "WHERE user_id = ?";

    private static final String findAllFriends = "SELECT * " +
                                                    "FROM user_friends";

    private static final String delete = "DELETE " +
                                            "FROM user_friends " +
                                            "WHERE user_id = ? AND friend_id = ?";

    private static final String deleteAllUserFriends = "DELETE " +
                                                        "FROM user_friends " +
                                                        "WHERE user_id = ? " +
                                                        "OR friend_id = ?";

    public FriendDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper, User.class);
    }

    public void addFriend(int userId, int friendId) {
        try {
            update(add, userId, friendId);
        } catch (DataIntegrityViolationException e) {
            throw new FindingException(e.getMessage());
        }
    }

    public Set<Integer> getFriendsId(int userId) {
        List<Integer> friends = jdbc.queryForList(findFriendsById, Integer.TYPE, userId);
        return new HashSet<>(friends);
    }

    public Map<Integer, Set<Integer>> getAllFriendsId() {
        Map<Integer, Set<Integer>> friends = new HashMap<>();
        return jdbc.query(findAllFriends, (ResultSet resultSet) -> {
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
            update(delete, userId, friendId);
        } catch (InternalServerException e) {
            throw new FindingException(e.getMessage());
        }
    }

    public void deleteAllUserFriends(int userId) {
        update(deleteAllUserFriends, userId, userId);
    }
}
