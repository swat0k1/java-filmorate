package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.service.UserFeedService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Validated
@AllArgsConstructor
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserFeedService userFeedService;

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@Valid @RequestBody User user) {
        User updatedUser = userService.updateUser(user);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public User delete(@PathVariable("id") int id) {
        return userService.delete(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") int id) {
        User user = userService.getUserById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Collection<User>> getAllUsers() {
        Collection<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        userService.addFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> removeFriend(@PathVariable("id") int id, @PathVariable("friendId") int friendId) {
        userService.removeFriend(id, friendId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Collection<User>> getFriends(@PathVariable("id") int id) {
        Collection<User> friends = userService.getFriends(id);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }

    @GetMapping("/{id}/friends/common/{friendId}")
    public ResponseEntity<Collection<User>> getCommonFriends(@PathVariable("id") int id, @PathVariable("friendId") int id2) {
        Collection<User> commonFriends = userService.getCommonFriends(id, id2);
        return new ResponseEntity<>(commonFriends, HttpStatus.OK);
    }

    @GetMapping("/{id}/recommendations")
    public ResponseEntity<Collection<Film>> getRecommendations(@PathVariable("id") int id) {
        Collection<Film> recommendedFilms = userService.getRecommendations(id);
        return new ResponseEntity<>(recommendedFilms, HttpStatus.OK);
    }

    @GetMapping("/{id}/feed")
    public ResponseEntity<Collection<UserFeed>> getUserFeeds(@PathVariable("id") int id) {
        Collection<UserFeed> userFeed = userFeedService.getUserFeed(id);
        return new ResponseEntity<>(userFeed, HttpStatus.OK);
    }
}
