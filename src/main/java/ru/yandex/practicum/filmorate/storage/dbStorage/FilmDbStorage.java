package ru.yandex.practicum.filmorate.storage.dbStorage;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FindingException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.sql.Timestamp;
import java.util.*;

@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private MpaDbStorage mpaDbStorage;
    private GenreDbStorage genreDbStorage;
    private LikeDbStorage likeDbStorage;

    private static final String INSERT = "INSERT " +
                                        "INTO films (name, description, release_date, duration, rating_id) " +
                                        "VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE = "UPDATE films " +
                                        "SET name = ?, description = ?, " +
                                        "release_date = ?, duration = ?, rating_id = ? " +
                                        "WHERE id = ?";

    private static final String FIND_ALL = "SELECT * " +
                                            "FROM films f, rating_mpa m " +
                                            "WHERE f.rating_id = m.mpa_id";

    private static final String FIND_BY_ID = "SELECT * " +
                                            "FROM films f, rating_mpa m " +
                                            "WHERE f.rating_id = m.mpa_id AND f.id = ?";

    private static final String DELETE = "DELETE " +
                                        "FROM films " +
                                        "WHERE id = ?";

    private static final String FIND_TOP_FILMS = "SELECT * " +
                                                    "FROM films f " +
                                                    "LEFT JOIN rating_mpa m " +
                                                    "ON f.id = m.mpa_id " +
                                                    "LEFT JOIN (" +
                                                        "SELECT film_id, COUNT(film_id) AS likes " +
                                                        "FROM film_likes " +
                                                        "GROUP BY film_id) fl " +
                                                    "ON f.id = fl.film_id ORDER BY likes DESC LIMIT ?";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, MpaDbStorage mpaDbStorage,
                         GenreDbStorage genreDbStorage, LikeDbStorage likeDbStorage) {
        super(jdbc, mapper, Film.class);
        this.mpaDbStorage = mpaDbStorage;
        this.genreDbStorage = genreDbStorage;
        this.likeDbStorage = likeDbStorage;
    }

    @Override
    public Film addFilm(Film film) {
        Mpa mpa = mpaDbStorage.findById(film.getMpa().getId())
                .orElseThrow(() -> new FindingException("Рейтинг не найден!."));
        Collection<Genre> genres = genreDbStorage.findManyByList(film.getGenres());
        int id = insert(
                INSERT,
                film.getName(),
                film.getDescription(),
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        film.setMpa(mpa);
        film.setGenres(new LinkedHashSet<>(genres));
        genreDbStorage.setFilmGenres(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        mpaDbStorage.findById(film.getMpa().getId());
        update(
                UPDATE,
                film.getName(),
                film.getDescription(),
                Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );
        return getFilmById(film.getId());
    }

    @Override
    public Collection<Film> getAllFilms() {
        Collection<Film> films = findMany(FIND_ALL);
        Map<Integer, Set<Genre>> genres = genreDbStorage.findAllFilmsGenres();
        Map<Integer, Collection<Integer>> likes = likeDbStorage.findAllFilmsLikes();
        for (Film film : films) {
            film.setGenres(genres.getOrDefault(film.getId(), new LinkedHashSet<>()));
            film.setLikes(likes.getOrDefault(film.getId(), new ArrayList<>()));
        }
        return films;
    }

    @Override
    public Film getFilmById(int id) {
        Film film = findOne(FIND_BY_ID, id)
                .orElseThrow(() -> new FindingException("Фильм с id = " + id + " не найден"));
        film.setGenres(new HashSet<>(genreDbStorage.findByFilmId(id)));
        film.setLikes(likeDbStorage.getFilmLikes(id));
        return film;
    }

    @Override
    public Film deleteFilm(int id) {
        Film film = getFilmById(id);
        genreDbStorage.deleteGenres(id);
        if (!film.getLikes().isEmpty()) likeDbStorage.deleteAllFilmLikes(id);
        update(DELETE, id);
        return film;
    }

    @Override
    public Collection<Film> getTopFilms(int count) {
        Collection<Film> films = findMany(FIND_TOP_FILMS, count);
        Map<Integer, Set<Genre>> genres = genreDbStorage.findAllFilmsGenres();
        Map<Integer, Collection<Integer>> likes = likeDbStorage.findAllFilmsLikes();
        for (Film film : films) {
            film.setGenres(genres.getOrDefault(film.getId(), new LinkedHashSet<>()));
            film.setLikes(likes.getOrDefault(film.getId(), new ArrayList<>()));
        }
        return films;
    }
}
