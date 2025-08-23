package ru.yandex.practicum.filmorate.storage.inMemoryStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FindingException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Component
@Deprecated
public class InMemoryFilmStorage /*implements FilmStorage*/ {

    private final ArrayList<Film> films = new ArrayList<>();
    private int currentId = 1;

    public Film addFilm(Film film) {
        validateFilm(film);
        film.setId(currentId);
        currentId++;
        films.add(film);
        log.info("Фильм добавлен: {}", film);
        return film;
    }

    public Film updateFilm(int id, Film updatedFilm) {
        validateFilm(updatedFilm);
        Optional<Film> existFilm = films.stream().filter(film -> film.getId() == id).findFirst();

        if (existFilm.isPresent()) {
            Film film = existFilm.get();
            film.setName(updatedFilm.getName());
            film.setDescription(updatedFilm.getDescription());
            film.setReleaseDate(updatedFilm.getReleaseDate());
            film.setDuration(updatedFilm.getDuration());
            log.info("Фильм обновлен: {}", film);
            return film;
        } else {
            log.warn("Фильм с указанным id {} не был найден", id);
            throw new FindingException("{\n" +
                    " \"error\": \"Фильм с таким id не найден.\"\n" +
                    "}");
        }
    }

    public ArrayList<Film> getAllFilms() {
        return films;
    }

    public Film getFilmById(int id) {
        return films.stream().filter(film -> film.getId() == id).findFirst().orElse(null);
    }

    public void deleteFilm(int id) {
        films.removeIf(film -> film.getId() == id);
    }

    private void validateFilm(Film film) {

        if (film.getName() == null || film.getName().isEmpty()) {
            log.error("Ошибка валидации фильма: название не может быть пустым.");
            throw new ValidationException("{\n" +
                    "    \"error\": \"Название фильма не может быть пустым!\"\n" +
                    "}");
        }

        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Ошибка валидации фильма: максимальная длина описания — 200 символов.");
            throw new ValidationException("{\n" +
                    "    \"error\": \"Максимальная длина описания фильма - 200 символов!\"\n" +
                    "}");
        }

        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(LocalDate.of(1895, 11, 28))) {
            log.error("Ошибка валидации фильма: дата релиза не может быть раньше 28 декабря 1895 года.");
            throw new ValidationException("{\n" +
                    "    \"error\": \"Дата релиза фильма не может быть раньше 28.11.1895!\"\n" +
                    "}");
        }

        if (film.getDuration() < 0 || film.getDuration() == 0) {
            log.error("Ошибка валидации фильма: продолжительность фильма должна быть положительным числом.");
            throw new ValidationException("{\n" +
                    "    \"error\": \"Продолжительность фильма должна быть положительным числом!\"\n" +
                    "}");
        }

    }

}
