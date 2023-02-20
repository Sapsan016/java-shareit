package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserServiceImpl implements UserService {

    UserRepository userRepository;


    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(long id) {
        return userRepository.findById(id);
    }

    @Override
    public User updateUser(long userId, UserAddDto userAddDto) {                               //Обновить пользователя
        User user = UserMapper.toUser(userAddDto);
        User userToUpdate = getUserById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь с id %s не найден", userId)));
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

    @Override
    public User addUser(UserAddDto userAddDto) {                                                //Добавить пользователя
        User user = UserMapper.toUser(userAddDto);
        userRepository.save(user);
        log.info("Пользователь с Id = {} создан", user.getId());
        return user;
    }

    @Override
    public void deleteUserById(long id) {                                                        //Удалить пользователя
        userRepository.deleteById(id);
        log.info("Пользователь с Id = {} удален", id);
    }

    private boolean checkEmail(User user) {                                                           //Проверить email
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.getEmail().equals(user.getEmail())) {
                return true;
            }
        }
        return false;
    }
}