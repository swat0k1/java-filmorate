package ru.yandex.practicum.filmorate.storage.dbStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FindingException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;

@Repository
@Slf4j
public class DirectorDbStorage extends BaseRepository<Director> {

    private static final String GET_DIRECTOR = "SELECT * " +
                                                "FROM director " +
                                                "WHERE id = ?";

    private static final String GET_ALL_DIRECTORS = "SELECT * " +
                                                    "FROM director";

    private static final String ADD_DIRECTOR = "INSERT " +
                                                "INTO director (id, director_name) " +
                                                "VALUES (?, ?)";

    private static final String UPDATE_DIRECTOR = "UPDATE director " +
                                                    "SET director_name = ? " +
                                                    "WHERE id = ?";

    private static final String DELETE_ALL_DIRECTORS = "DELETE " +
                                                        "FROM director";

    private static final String DELETE_DIRECTOR_BY_ID = "DELETE " +
                                                        "FROM director " +
                                                        "WHERE id = ?";

    private static final String DELETE_ALL_FILM_DIRECTOR = "DELETE " +
                                                            "FROM film_director";

    private static final String DELETE_DIRECTOR_FROM_FILM_BY_DIRECTOR_ID = "DELETE " +
                                                                            "FROM film_director " +
                                                                            "WHERE director_id = ?";

    private static final String GET_LAST_ID = "SELECT MAX(id) " +
                                                "FROM director";

    private static final String GET_FILMS_DIRECTOR = "SELECT id, director_name " +
                                                    "FROM director d, film_director fd " +
                                                    "WHERE d.id = fd.director_id AND fd.film_id = ?";



    public DirectorDbStorage(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper, Director.class);
    }

    public Director getDirector(int directorId) {

        try {
            return findOne(GET_DIRECTOR, directorId)
                    .orElseThrow(() -> new FindingException("Режиссер с id = " + directorId + " не найден"));
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка получения режиссера.");
        }

    }

    public List<Director> getAllDirectors() {
        try {
            return findMany(GET_ALL_DIRECTORS);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка получения режиссеров.");
        }
    }

    public Director addDirector(Director director) {

        try {
            if (director.getId() == 0) {
                director.setId(getNextId());
            }

            int id = insert(ADD_DIRECTOR,
                    director.getId(),
                    director.getName());

            director.setId(id);

            return director;
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка добавления режиссера.");
        }

    }

    public Director updateDirector(Director director) {

        try {
            if (director.getId() >= getNextId()) {
                throw new FindingException("Режиссер с id = " + director.getId() + "не найден");
            }

            update(UPDATE_DIRECTOR,
                    director.getName(),
                    director.getId());

            return getDirector(director.getId());

        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка обновления режиссера.");
        }

    }

    public void deleteDirector(int directorId) {

        try {
            update(DELETE_DIRECTOR_BY_ID, directorId);
        } catch (InternalServerException e) {
            throw new FindingException("Ошибка удаления режиссера.");
        }

    }

    public void deleteAllDirectors() {

        try {
            update(DELETE_ALL_DIRECTORS);
        } catch (InternalServerException e) {
            throw new FindingException("Ошибка удаления режиссеров.");
        }

    }

    public int getNextId() {

        Integer nextId = jdbc.queryForObject(GET_LAST_ID, Integer.class);

        if (nextId == null) {
            return 1;
        } else {
            return nextId + 1;
        }

    }

    public Collection<Director> getDirectorOfFilm(int filmId) {

        try {
            return findMany(GET_FILMS_DIRECTOR, filmId);
        } catch (InternalServerException e) {
            throw new InternalServerException("Ошибка получения режиссера фильма.");
        }

    }

}
