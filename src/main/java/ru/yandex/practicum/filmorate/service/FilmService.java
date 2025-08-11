package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FindingException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(int id, Film updatedFilm) {
        return filmStorage.updateFilm(id, updatedFilm);
    }

    public ArrayList<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (film == null) {
            throw new FindingException("{\n" +
                    "    \"error\": \"Фильм не найден!\"\n" +
                    "}");
        }

        if (user == null) {
            throw new FindingException("{\n" +
                    "    \"error\": \"Пользователь не найден!\"\n" +
                    "}");
        }

        if (film.getLikes().add(userId)) {
            log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        } else {
            throw new ValidationException("{\n" +
                    "    \"error\": \"Пользователь уже поставил лайк этому фильму.\"\n" +
                    "}");
        }
    }

    public void removeLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);

        if (film == null) {
            throw new FindingException("{\n" +
                    "    \"error\": \"Фильм не найден!\"\n" +
                    "}");
        }

        if (film.getLikes().remove(userId)) {
            log.info("Пользователь {} убрал лайк с фильма {}", userId, filmId);
        } else {
            throw new FindingException("{\n" +
                    "    \"error\": \"Пользователь не ставил лайк этому фильму.\"\n" +
                    "}");
        }
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> Integer.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

}
