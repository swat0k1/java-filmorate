package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FindingException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class GenreDbStorage extends BaseRepository<Genre> {

    private static final String insert = "INSERT " +
                                        "INTO film_genres (film_id, genre_id) " +
                                        "VALUES (?, ?)";

    private static final String findById = "SELECT * " +
                                            "FROM genres " +
                                            "WHERE id = ?";

    private static final String findMany = "SELECT * " +
                                            "FROM genres " +
                                            "WHERE id IN (%s)";

    private static final String findAll = "SELECT * " +
                                            "FROM genres";

    private static final String findByFilmId = "SELECT id, name " +
                                                    "FROM genres g, film_genres fg " +
                                                    "WHERE g.id = fg.genre_id AND fg.film_id = ?";

    private static final String findAllFilmsGenres = "SELECT film_id, genre_id, name " +
                                                        "FROM film_genres fg, " +
                                                        "genres g " +
                                                        "WHERE fg.genre_id = g.id";

    private static final String deleteAllGenresFilm = "DELETE " +
                                                        "FROM film_genres " +
                                                        "WHERE film_id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
    }

    public void setFilmGenres(int filmId, Collection<Genre> genres) {
        if (genres.isEmpty()) return;
        List<Integer> genresId = new ArrayList<>(genres.stream().map(Genre::getId).toList());
        batchUpdateBase(insert, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setInt(2, genresId.get(i));
            }

            @Override
            public int getBatchSize() {
                return genresId.size();
            }
        });
    }

    public Optional<Genre> findById(int genreId) {
        return findOne(findById, genreId);
    }

    public Collection<Genre> findManyByList(Collection<Genre> genres) {
        if (genres.isEmpty()) return new ArrayList<>();
        List<Integer> genresId = new ArrayList<>(genres.stream().map(Genre::getId).toList());
        String inSql = String.join(",", Collections.nCopies(genresId.size(), "?"));

        List<Genre> result = jdbc.query(
                String.format(findMany, inSql),
                genresId.toArray(),
                rowMapper);
        if (result.size() != genresId.size()) throw new FindingException("Жанр не найден!");
        return result;
    }

    public Collection<Genre> findAll() {
        return findMany(findAll);
    }

    public Collection<Genre> findByFilmId(long filmId) {
        return findMany(findByFilmId, filmId);
    }

    public Map<Integer, Set<Genre>> findAllFilmsGenres() {
        Map<Integer, Set<Genre>> genres = new HashMap<>();
        return jdbc.query(findAllFilmsGenres, (ResultSet resultSet) -> {
            while (resultSet.next()) {
                Integer filmId = resultSet.getInt("film_id");
                Integer genreId = resultSet.getInt("genre_id");
                String genreName = resultSet.getString("name");
                genres.computeIfAbsent(filmId, k -> new LinkedHashSet<>()).add(new Genre(genreId, genreName));
            }
            return genres;
        });
    }

    public void deleteGenres(int filmId) {
        update(deleteAllGenresFilm, filmId);
    }
}
