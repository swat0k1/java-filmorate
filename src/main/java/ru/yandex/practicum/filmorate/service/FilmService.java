package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.dbStorage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.UserFeedDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeDbStorage likeDbStorage;
    private final UserFeedDbStorage userFeedDbStorage;

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film updatedFilm) {
        return filmStorage.updateFilm(updatedFilm);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @Transactional
    public Film addLike(int filmId, int userId) {
        likeDbStorage.addLike(filmId, userId);
        userFeedDbStorage.save(new UserFeed(userId, EventType.LIKE,
                Operation.ADD, filmId));
        return filmStorage.getFilmById(filmId);
    }

    @Transactional
    public Film removeLike(int filmId, int userId) {
        likeDbStorage.deleteLike(filmId, userId);
        userFeedDbStorage.save(new UserFeed(userId, EventType.LIKE,
                Operation.REMOVE, filmId));
        return filmStorage.getFilmById(filmId);
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> getFilmByDirector(int id, String sort) {

        List<Film> films;

        if (sort.equals("year") || sort.equals("likes")) {
            films = filmStorage.getFilmsByDirector(id, sort);
        } else {
            films = filmStorage.getFilmsByDirector(id, "year");
        }

        return films;

    }

    public Collection<Film> getTopFilms(Integer count, Integer genreId, Integer year) {
        return filmStorage.getTopFilms(count, genreId, year);
    }

    public Film delete(int id) {
        return filmStorage.deleteFilm(id);
    }
}
