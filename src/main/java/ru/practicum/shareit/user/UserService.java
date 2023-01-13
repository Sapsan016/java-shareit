package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public UserDto getUserById(long id) {
        return userRepository.getUserById(id);
    }

    public UserDto updateUser(long userId, User user) {
        return userRepository.updateUser(userId, user);
    }

    public UserDto createUser(User user) {
        return userRepository.createUser(user);
    }

    public void deleteUserById(long id) {
        userRepository.deleteUserById(id);
    }
}
