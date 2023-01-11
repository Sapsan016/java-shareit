package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@Component
public interface UserRepository {
    UserDto getUserById(long id);
    List<UserDto> getAllUsers();
    UserDto createUser(@Valid @RequestBody User user);
    UserDto updateUser( long userId, @Valid @RequestBody User user);
    void deleteUserById(long id);

}