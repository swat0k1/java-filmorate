package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Validated
@Slf4j
@AllArgsConstructor
public class FilmController {

    @Autowired
    private FilmService filmService;

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        Film createdFilm = filmService.addFilm(film);
        return new ResponseEntity<>(createdFilm, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        Film updatedFilm = filmService.updateFilm(film);
        return new ResponseEntity<>(updatedFilm, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable("id") int id) {
        Film film = filmService.getFilmById(id);
        if (film == null) {
            throw new ValidationException("Фильм не найден!");
        }
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmByDirector(@PathVariable int directorId, @RequestParam String sortBy) {
        return filmService.getFilmByDirector(directorId, sortBy);
    }

    @GetMapping
    public ResponseEntity<Collection<Film>> getAllFilms() {
        Collection<Film> films = filmService.getAllFilms();
        return new ResponseEntity<>(films, HttpStatus.OK);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Film> addLike(@PathVariable("filmId") int filmId, @PathVariable("userId") int userId) {
        filmService.addLike(filmId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public ResponseEntity<Film> removeLike(@PathVariable("filmId") int filmId, @PathVariable("userId") int userId) {
        filmService.removeLike(filmId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/popular")
    public ResponseEntity<Collection<Film>> getTopFilms(
            @RequestParam(name = "count", required = false) @Positive Integer count,
            @RequestParam(name = "genreId", required = false) @Positive Integer genreId,
            @RequestParam(name = "year", required = false) @Positive Integer year) {
        Collection<Film> topFilms = filmService.getTopFilms(count, genreId, year);
        return new ResponseEntity<>(topFilms, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public Film delete(@PathVariable("id") int id) {
        return filmService.delete(id);
    }

    @GetMapping("/search")
    public ResponseEntity<Collection<Film>> getFoundFilms(
            @RequestParam(name = "query", required = false) String textForSearch,
            @RequestParam(name = "by", required = false) List<String> by
    ) {
        Collection<Film> foundFilmsSortedByPopularity = filmService.getFoundFilms(textForSearch, by);
        return new ResponseEntity<>(foundFilmsSortedByPopularity, HttpStatus.OK);
    }
}
