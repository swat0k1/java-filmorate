package ru.yandex.practicum.filmorate.storage.interfaces;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Collection<Film> getAllFilms();

    Film getFilmById(int id);

    Film deleteFilm(int id);

    List<Film> getFilmsByDirector(int id, String sort);

    Collection<Film> getTopFilms(Integer count, Integer genreId, Integer year);

    Collection<Film> getCommonFilms(int userId, int friendId);

    Collection<Film> searchFilms(String query, List<String> by);
}
