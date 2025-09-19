package ru.yandex.practicum.filmorate.exception;

public class MissingQueryException extends RuntimeException {
    public MissingQueryException(String message) {
        super(message);
    }
}
