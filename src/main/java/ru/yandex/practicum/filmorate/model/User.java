package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {

    private int id;
    @NotNull(message = "Электронная почта не может быть пустой.")
    @Email(message = "Электронная почта должна быть корректного формата.")
    private String email;
    @NotBlank(message = "Логин не может быть пустым.")
    @Pattern(regexp = "^[\\S]+$", message = "Логин не может содержать пробелы.")
    private String login;
    private String name;
    @Past(message = "Дата рождения не может быть больше текущей даты.")
    private LocalDate birthday;

}
