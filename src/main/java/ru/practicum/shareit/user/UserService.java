package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    Optional<User> getUserById(long id);

    User updateUser(long userId, User user);

    User createUser(User user);

    void deleteUserById(long id);
}
