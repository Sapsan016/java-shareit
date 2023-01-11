package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Repository
@Slf4j
public class InMemoryUserRepository implements UserRepository {
    @NonFinal
    long generatedId = 0L;

    private long getGeneratedId() {
        return ++generatedId;
    }

    Map<Long, User> users = new HashMap<>();


    @Override
    public UserDto getUserById(long id) {
        if (isPresent(id)) {
            return UserMapper.toUserDto(users.get(id));
        }
        throw new UserNotFoundException("Пользователь не найден");
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<UserDto> usersDtos = new ArrayList<>();
        for (User u : users.values()) {
            usersDtos.add(UserMapper.toUserDto(u));
        }
        return usersDtos;
    }

    @Override
    public UserDto createUser(User user) {
        if(checkEmail(user)) {
            throw new ValidationException("Пользователь с таким email уже существует!");
        }
        user.setId(getGeneratedId());
        users.put(user.getId(), user);
        log.info("Пользователь с Id = {} создан", user.getId());
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(long userId, User userToUpdate) {
        if (isPresent(userId)) {
            User user = users.get(userId);

            if(checkEmail(userToUpdate)) {
                log.error("Такие данные уже существуют");
                throw new ValidationException("Такие данные уже существуют");
            }
            if (userToUpdate.getName() != null )
                user.setName(userToUpdate.getName());
            if (userToUpdate.getEmail() != null)
                user.setEmail(userToUpdate.getEmail());
            users.put(userId, user);
            log.info("Пользователь с Id = {} обновлен", userId);
            return UserMapper.toUserDto(user);
        }
        throw new UserNotFoundException("Пользователь не найден");
    }

    @Override
    public void deleteUserById(long id) {
        users.remove(id);
        log.info("Пользователь с Id = {} удален", id);
    }

    private boolean isPresent(long id) {
        if (users.containsKey(id)) {
            return true;
        } else {
            log.info("Пользователь с Id = {} не найден", id);
            throw new UserNotFoundException("Пользователь не найден");
        }
    }

    private boolean checkEmail(User user) {
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail())) {
                return true;
            }
        }
        return false;
    }
}
