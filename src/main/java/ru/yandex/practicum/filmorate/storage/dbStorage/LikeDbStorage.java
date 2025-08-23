package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
public class LikeDbStorage extends BaseRepository<Film> {

    private static final String INSERT = "INSERT " +
                                        "INTO film_likes (film_id, user_liked_id) " +
                                        "VALUES (?, ?)";

    private static final String FIND_FILM_LIKES = "SELECT user_liked_id " +
                                                    "FROM film_likes " +
                                                    "WHERE film_id = ?";

    private static final String DELETE = "DELETE " +
                                            "FROM film_likes " +
                                            "WHERE film_id = ? AND user_liked_id = ?";

    private static final String DELETE_ALL_FILM_LIKES = "DELETE " +
                                                        "FROM film_likes " +
                                                        "WHERE film_id = ?";

    private static final String FIND_ALL_FILMS_LIKES = "SELECT * " +
                                                        "FROM film_likes";

    public LikeDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper, Film.class);
    }

    public void addLike(int filmId, int userId) {
        update(INSERT, filmId, userId);
    }

    public Collection<Integer> getFilmLikes(int filmId) {
        return jdbc.queryForList(FIND_FILM_LIKES, Integer.TYPE, filmId);
    }

    public void deleteLike(int filmId, int userId) {
        update(DELETE, filmId, userId);
    }

    public void deleteAllFilmLikes(int filmId) {
        update(DELETE_ALL_FILM_LIKES, filmId);
    }

    public Map<Integer, Collection<Integer>> findAllFilmsLikes() {
        Map<Integer, Collection<Integer>> likes = new HashMap<>();
        return jdbc.query(FIND_ALL_FILMS_LIKES, (ResultSet resultSet) -> {
            while (resultSet.next()) {
                int filmId = resultSet.getInt("film_id");
                int likeId = resultSet.getInt("user_liked_id");
                likes.computeIfAbsent(filmId, k -> new ArrayList<>()).add(likeId);
            }
            return likes;
        });
    }
}
