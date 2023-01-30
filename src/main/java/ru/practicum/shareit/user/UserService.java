package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserAddDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    Optional<User> getUserById(long id);

    User updateUser(long userId, UserAddDto userAddDto);

    User addUser(UserAddDto userAddDto);

    void deleteUserById(long id);
}
