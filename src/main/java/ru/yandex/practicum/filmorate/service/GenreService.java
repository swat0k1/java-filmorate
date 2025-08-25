package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FindingException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dbStorage.GenreDbStorage;

import java.util.Collection;

@Service
@AllArgsConstructor
public class GenreService {

    private GenreDbStorage genreDbStorage;

    public Collection<Genre> findAll() {
        return genreDbStorage.findAll();
    }

    public Genre findById(Integer id) {
        return genreDbStorage.findById(id)
                .orElseThrow(() -> new FindingException("Genre с id = " + id + " не найден"));
    }
}
