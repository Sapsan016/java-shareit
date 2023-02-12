package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.user.dto.UserAddDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserControllerTest {
    @Autowired
    @NonFinal
    ObjectMapper mapper;
    @MockBean
    @NonFinal
    UserServiceImpl userService;
    @Autowired
    @NonFinal
    MockMvc mvc;

    UserAddDto userAddDto = new UserAddDto("John", "john.doe@mail.com");

    UserAddDto userUpdateAddDto = new UserAddDto("Ivan", "ivan.ivanov@mail.com");

    User user = new User(1L, "John", "john.doe@mail.com");

    User user2 = new User(2L, "Mike", "mike.gor@mail.com");

    User updatedUser = new User(1L, "Ivan", "ivan.ivanov@mail.com");


    @Test
    void addNewUser() throws Exception {
        when(userService.addUser(any()))
                .thenReturn(user);
        String expectedResponse = mapper.writeValueAsString(UserMapper.toUserDto(user));
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userAddDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getAllUsers() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(user, user2));
        mvc.perform(
                        get("/users"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(mapper.writeValueAsString(List.of(user, user2))));
    }

    @Test
    void getUserById() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(Optional.of(user));
        String expectedResponse = mapper.writeValueAsString(UserMapper.toUserDto(user));
        mvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void updateUser() throws Exception {
        when(userService.updateUser(1L, userUpdateAddDto))
                .thenReturn(updatedUser);
        String expectedResponse = mapper.writeValueAsString(UserMapper.toUserDto(updatedUser));
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userUpdateAddDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void userNotFound() throws Exception {
        when(userService.getUserById(anyLong()))
                .thenReturn(Optional.empty());
        mvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }
}