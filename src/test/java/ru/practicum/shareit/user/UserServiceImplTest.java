package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserAddDto;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImplTest {

    UserServiceImpl userService;

    UserAddDto userAddDto1 = new UserAddDto("testUser1", "user1@email.com");

    UserAddDto userAddDto2 = new UserAddDto("testUser2", "user2@email.com");

    UserAddDto updatedUser = new UserAddDto("UpdatedUser", "update@yandex.ru");

    @Test
    void saveUser() {
        User savedUser = userService.addUser(userAddDto1);
        assertThat(savedUser.getId(), equalTo(1L));
        assertThat(savedUser.getName(), equalTo(userAddDto1.getName()));
        assertThat(savedUser.getEmail(), equalTo(userAddDto1.getEmail()));
    }

    @Test
    void getAllUsers() {
        userService.addUser(userAddDto1);
        userService.addUser(userAddDto2);
        List<User> users = userService.getAllUsers();
        assertThat(users, hasSize(2));
        assertThat(users.get(0).getName(), equalTo(userAddDto1.getName()));
        assertThat(users.get(1).getEmail(), equalTo(userAddDto2.getEmail()));

    }

    @Test
    void updateUser() {
        User savedUser = userService.addUser(userAddDto1);
        User savedUpdUser = userService.updateUser(savedUser.getId(), updatedUser);
        assertThat(updatedUser.getName(), equalTo(savedUpdUser.getName()));
        assertThat(updatedUser.getEmail(), equalTo(savedUpdUser.getEmail()));
    }

    @Test
    void updateFailedUserNotFound() {
        try {
            userService.updateUser(99L, updatedUser);
        } catch (UserNotFoundException e) {
            assertThat(("Пользователь с id " + 99 + " не найден"), equalTo(e.getMessage()));
        }
    }

    @Test
    void updateFailedEmail() {
        User savedUser = userService.addUser(userAddDto1);
        updatedUser.setEmail(updatedUser.getEmail());
        try {
            userService.updateUser(savedUser.getId(), updatedUser);
        } catch (ValidationException e) {
            assertThat(("Такие данные уже существуют"), equalTo(e.getMessage()));
        }
    }

    @Test
    void deleteUser() {
        User savedUser = userService.addUser(userAddDto1);
        assertThat(savedUser, equalTo(userService.getUserById(savedUser.getId()).get()));
        userService.deleteUserById(savedUser.getId());
        try {
            userService.getUserById(savedUser.getId());
        } catch (UserNotFoundException e) {
            assertThat(("Пользователь с id " + savedUser.getId() + " не найден"), equalTo(e.getMessage()));
        }
    }
}