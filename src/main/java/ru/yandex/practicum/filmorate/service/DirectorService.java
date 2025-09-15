package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.dbStorage.DirectorDbStorage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorDbStorage directorDbStorage;

    public Director getDirector(int id) {
        return directorDbStorage.getDirector(id);
    }

    public List<Director> getAllDirectors() {
        return directorDbStorage.getAllDirectors();
    }

    public Director addDirector(Director director) {
        return directorDbStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorDbStorage.updateDirector(director);
    }

    public void deleteDirector(int id) {
        directorDbStorage.deleteDirector(id);
    }

    public void deleteAll() {
        directorDbStorage.deleteAllDirectors();
    }
}
