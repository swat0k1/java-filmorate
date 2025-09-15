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

    Collection<Film> getTopFilms(int count);

    public List<Film> getFilmsByDirector(int id, String sort);

}
