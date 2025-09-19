package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dbStorage.LikeDbStorage;
import ru.yandex.practicum.filmorate.storage.interfaces.FilmStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikeDbStorage likeDbStorage;
    private final DirectorService directorService;

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film updatedFilm) {
        return filmStorage.updateFilm(updatedFilm);
    }

    public Collection<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addLike(int filmId, int userId) {
        likeDbStorage.addLike(filmId, userId);
        return filmStorage.getFilmById(filmId);
    }

    public Film removeLike(int filmId, int userId) {
        likeDbStorage.deleteLike(filmId, userId);
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

    public Collection<Film> getFoundFilms(String textForSearch, List<String> by) {

        List<Film> films = getAllFilms().stream().toList();

        Stream<Film> filmsSortedByDirector =
                films.stream()
                        .filter(film -> film.getDirectors().stream()
                                .anyMatch(director -> director.getName().contains(textForSearch)))
                        .sorted(Comparator.comparingInt(film -> film.getLikes().size()));

        Stream<Film> filmsSortedByTitle = getAllFilms().stream()
                .filter(film -> film.getName().contains(textForSearch));

        if (by.size() == 1) {
            switch (by.getFirst()) {
                case "director" -> {
                    return filmsSortedByDirector.toList();
                }

                case "title" -> {
                    return filmsSortedByTitle.toList();
                }
            }
        }

        return Stream.concat(filmsSortedByTitle, filmsSortedByDirector).toList();
    }
}
