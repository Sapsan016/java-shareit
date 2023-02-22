package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
     //   properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImplTest {

    UserService userService;
    UserAddDto userAddDto1 = new UserAddDto("testUser1", "user1@email.com");
    UserAddDto userAddDto2 = new UserAddDto("testUser2", "user2@email.com");
    UserAddDto updatedUser = new UserAddDto("UpdatedUser", "update@yandex.ru");
    @NonFinal
    User savedUser;

    @BeforeEach
    void setUp() {
        savedUser = userService.addUser(userAddDto1);
    }

    @Test
    void saveUser() {
        assertThat(savedUser.getId(), notNullValue());
        assertThat(savedUser.getName(), equalTo(userAddDto1.getName()));
        assertThat(savedUser.getEmail(), equalTo(userAddDto1.getEmail()));
    }

    @Test
    void getAllUsers() {
        userService.addUser(userAddDto2);
        List<User> users = userService.getAllUsers();
        assertThat(users, hasSize(2));
        assertThat(users.get(0).getName(), equalTo(userAddDto1.getName()));
        assertThat(users.get(1).getEmail(), equalTo(userAddDto2.getEmail()));

    }

    @Test
    void updateUser() {
        User savedUpdUser = userService.updateUser(savedUser.getId(), updatedUser);
        assertThat(updatedUser.getName(), equalTo(savedUpdUser.getName()));
        assertThat(updatedUser.getEmail(), equalTo(savedUpdUser.getEmail()));
    }

    @Test
    void updateFailedUserNotFound() {
        try {
            userService.updateUser(99L, updatedUser);
        } catch (UserNotFoundException e) {
            assertThat((String.format("Пользователь с id %s не найден", 99L)), equalTo(e.getMessage()));
        }
    }

    @Test
    void updateFailedEmail() {
        updatedUser.setEmail(updatedUser.getEmail());
        try {
            userService.updateUser(savedUser.getId(), updatedUser);
        } catch (ValidationException e) {
            assertThat(("Такие данные уже существуют"), equalTo(e.getMessage()));
        }
    }

    @Test
    void deleteUser() {
        assertThat(savedUser, equalTo(userService.getUserById(savedUser.getId()).get()));
        userService.deleteUserById(savedUser.getId());
        try {
            userService.getUserById(savedUser.getId());
        } catch (UserNotFoundException e) {
            assertThat((String.format("Пользователь с id %s не найден", savedUser.getId())), equalTo(e.getMessage()));
        }
    }
}