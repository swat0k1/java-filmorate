package ru.yandex.practicum.filmorate.dbStorageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dbStorage.FriendDbStorage;
import ru.yandex.practicum.filmorate.storage.dbStorage.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Collection;

@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import({UserDbStorage.class, FriendDbStorage.class, FriendDbStorage.class, UserRowMapper.class})
@JdbcTest
public class UserDbStorageTest {

    private final UserDbStorage userDbStorage;
    private final FriendDbStorage friendDbStorage;

    private User user1;
    private User user2;
    private User user3;

    @BeforeEach
    void setUp() {
        user1 = createUser(1,"test1@test.ru", "test1", "test1", LocalDate.of(2001, 1, 1));
        user2 = createUser(2,"test2@test.ru", "test2", "test2", LocalDate.of(2002, 1, 1));
        user3 = createUser(3,"test3@test.ru", "test3", "test3", LocalDate.of(2003, 1, 1));
    }

    private User createUser(int id, String email, String login, String name, LocalDate birthday) {
        return new User(id, email, login, name, birthday);
    }

    @Test
    void shouldCreateAndFindById() {
        User createdUser = userDbStorage.createUser(user1);
        assertThat(userDbStorage.getUserById(createdUser.getId())).hasFieldOrPropertyWithValue("id", createdUser.getId());
    }

    @Test
    void shouldFindAllUsers() {
        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);
        assertThat(userDbStorage.getAllUsers()).hasSize(2);
    }

    @Test
    void shouldUpdateUser() {
        User createdUser = userDbStorage.createUser(user1);
        createdUser.setName("Tested");
        userDbStorage.updateUser(createdUser);
        assertThat(userDbStorage.getUserById(createdUser.getId())).hasFieldOrPropertyWithValue("name", "Tested");
    }

    @Test
    void shouldDeleteUser() {
        User createdUser = userDbStorage.createUser(user1);
        userDbStorage.deleteUser(createdUser.getId());
        assertThat(userDbStorage.getAllUsers()).isEmpty();
    }

    @Test
    void shouldGetCommonFriends() {
        User createdUser1 = userDbStorage.createUser(user1);
        User createdUser2 = userDbStorage.createUser(user2);
        User createdUser3 = userDbStorage.createUser(user3);
        addFriends(createdUser1.getId(), createdUser2.getId(), createdUser3.getId());

        Collection<User> commonFriends = userDbStorage.getCommonFriends(createdUser1.getId(), createdUser2.getId());
        assertThat(commonFriends).hasSize(1);
        User friend = commonFriends.iterator().next();
        assertThat(friend).hasFieldOrPropertyWithValue("name", "test3");
    }

    @Test
    void shouldGetFriends() {
        User createdUser1 = userDbStorage.createUser(user1);
        User createdUser2 = userDbStorage.createUser(user2);
        User createdUser3 = userDbStorage.createUser(user3);
        addFriends(createdUser1.getId(), createdUser2.getId(), createdUser3.getId());

        assertThat(userDbStorage.getFriends(createdUser1.getId())).hasSize(2);
    }

    private void addFriends(int userId1, int userId2, int userId3) {
        friendDbStorage.addFriend(userId1, userId2);
        friendDbStorage.addFriend(userId1, userId3);
        friendDbStorage.addFriend(userId2, userId1);
        friendDbStorage.addFriend(userId2, userId3);
    }
}
