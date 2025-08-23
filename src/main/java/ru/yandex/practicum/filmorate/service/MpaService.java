package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FindingException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dbStorage.MpaDbStorage;

import java.util.Collection;

@Service
@AllArgsConstructor
public class MpaService {

    private MpaDbStorage mpaDbStorage;

    public Collection<Mpa> findAll() {
        return mpaDbStorage.findAll();
    }

    public Mpa getById(Integer id) {
        return mpaDbStorage.findById(id)
                .orElseThrow(() -> new FindingException("Mpa с id = " + id + " не найден"));
    }
}
