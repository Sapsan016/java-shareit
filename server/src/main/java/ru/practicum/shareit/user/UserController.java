package ru.practicum.shareit.user;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<UserDto> getAllUsers() {                                                  //Получить всех пользователей
        return userService.getAllUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")                                                                      //Получить пользователя
    public UserDto getUserById(@PathVariable("id") long id) {
        if (userService.getUserById(id).isPresent()) {
            return UserMapper.toUserDto(userService.getUserById(id).get());
        } else
            throw new UserNotFoundException("Пользователь не найден");
    }

    @PatchMapping("/{id}")                                                                   //Обновить пользователя
    public UserDto updateUser(@RequestBody UserAddDto userAddDto, @PathVariable("id") long userId) {
        return UserMapper.toUserDto(userService.updateUser(userId, userAddDto));
    }

    @PostMapping()                                                                           //Добавить пользователя
    public UserDto addUser(@RequestBody UserAddDto userAddDto) {
        return UserMapper.toUserDto(userService.addUser(userAddDto));
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") int id) {
        userService.deleteUserById(id);
    }
}