package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;

public interface FilmStorage {

    Film addFilm(Film film);

    Film updateFilm(int id, Film film);

    ArrayList<Film> getAllFilms();

    Film getFilmById(int id);

    void deleteFilm(int id);

}
