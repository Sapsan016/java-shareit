package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl {

    UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    public User updateUser(long userId, User user) {
        if (isPresentUser(userId)) {
            User userToUpdate = getUserById(userId).get();
            if (checkEmail(user)) {
                log.error("Такие данные уже существуют");
                throw new ValidationException("Такие данные уже существуют");
            }
            if (user.getName() != null)
                userToUpdate.setName(user.getName());
            if (user.getEmail() != null)
                userToUpdate.setEmail(user.getEmail());
            log.info("Пользователь с Id = {} обновлен", userId);
            return userRepository.save(userToUpdate);
        }
        log.error("Пользователь с Id = {} не найден", userId);
        throw new UserNotFoundException("Пользователь не найден");

    }

    public User createUser(User user) {
        log.info("Пользователь с Id = {} создан", user.getId());
        return userRepository.save(user);
    }

    public void deleteUserById(long id) {
        userRepository.deleteById(id);
        log.info("Пользователь с Id = {} удален", id);
    }

    private boolean checkEmail(User user) {
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.getEmail().equals(user.getEmail())) {
                return true;
            }
        }
        return false;
    }

    private boolean isPresentUser(long id) {
        return getUserById(id).isPresent();
    }
}
