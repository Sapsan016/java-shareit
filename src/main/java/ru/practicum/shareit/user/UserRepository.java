package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Component
public interface UserRepository {

    UserDto getUserById(long id);

    List<UserDto> getAllUsers();

    UserDto createUser(User user);

    UserDto updateUser(long userId, User user);

    void deleteUserById(long id);
}