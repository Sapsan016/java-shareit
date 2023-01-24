package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.UserNotFoundException;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping()
    public List<User> getAllUsers() {
        return userServiceImpl.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") long id) {
        if (userServiceImpl.getUserById(id).isPresent()) {
           return userServiceImpl.getUserById(id).get();
        }
        else
            throw new UserNotFoundException("Пользователь не найден");
    }

    @PatchMapping("/{id}")
    public User updateUser(@RequestBody User user, @PathVariable("id") long userId) {
        return userServiceImpl.updateUser(userId, user);
    }

    @PostMapping()
    public User createUser(@Valid @RequestBody User user) {
        return userServiceImpl.createUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable("id") int id) {
        userServiceImpl.deleteUserById(id);
    }
}
