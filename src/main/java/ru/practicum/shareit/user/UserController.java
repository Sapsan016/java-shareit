package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserServiceImpl userServiceImpl;

    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping()
    public List<UserDto> getAllUsers() {
        return userServiceImpl.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") long id) {
        if (userServiceImpl.getUserById(id).isPresent()) {
            return UserMapper.toUserDto(userServiceImpl.getUserById(id).get());
        } else
            throw new UserNotFoundException("Пользователь не найден");
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@RequestBody User user, @PathVariable("id") long userId) {
        return UserMapper.toUserDto(userServiceImpl.updateUser(userId, user));
    }

    @PostMapping()
    public UserDto createUser(@Valid @RequestBody User user) {
        return UserMapper.toUserDto(userServiceImpl.createUser(user));
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") int id) {
        userServiceImpl.deleteUserById(id);
    }
}