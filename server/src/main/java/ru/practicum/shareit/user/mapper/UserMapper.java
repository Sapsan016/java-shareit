package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.UserAddDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.model.User;

public class UserMapper {
    public static User toUser(UserAddDto userAddDto) {
        return new User(
                null,
                userAddDto.getName(),
                userAddDto.getEmail()
        );
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
