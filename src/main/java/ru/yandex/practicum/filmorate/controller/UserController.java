package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.FindingException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        User updatedUser = userService.updateUser(user.getId(), user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        User user = userService.getAllUsers().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ValidationException("{\n" +
                        "    \"error\": \"Пользователь не найден!\"\n" +
                        "}"));
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<ArrayList<User>> getAllUsers() {
        ArrayList<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/friends/confirm/{friendId}")
    public ResponseEntity<Void> confirmFriendship(@PathVariable int id, @PathVariable int friendId) {
        userService.confirmFriendship(id, friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.removeFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<List<User>> getFriends(@PathVariable int id) {
        User user = userService.getAllUsers().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElseThrow(() -> new ValidationException("{\n" +
                        "    \"error\": \"Пользователь не найден!\"\n" +
                        "}"));

        // Извлекаем объекты User для друзей
        List<User> friends = user.getFriends().keySet().stream()
                .map(friendId -> userService.getAllUsers().stream()
                        .filter(f -> f.getId() == friendId)
                        .findFirst()
                        .orElse(null))
                .filter(friend -> friend != null)
                .collect(Collectors.toList());

        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public ResponseEntity<List<User>> getCommonFriends(@PathVariable int id, @PathVariable int friendId) {
        List<User> commonFriends = userService.getCommonFriends(id, friendId);
        return new ResponseEntity<>(commonFriends, HttpStatus.OK);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FindingException.class)
    public ResponseEntity<String> handleFindingException(FindingException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

}
