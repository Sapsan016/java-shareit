package ru.practicum.shareit.usertest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserAddDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImplTest {

    UserRepository userRepository;

    long ID = 1L;

    UserAddDto userAddDto1 = new UserAddDto("testUser1", "user1@email.com");

    UserAddDto userAddDto2 = new UserAddDto("testUser2", "user2@email.com");




    @Test
    void saveUser() {
        userRepository.save(UserMapper.toUser(userAddDto1));
        User savedUser = userRepository.findById(ID).orElseThrow(() ->
                new UserNotFoundException("User not found"));
        assertThat(savedUser.getId(), notNullValue());
        assertThat(savedUser.getName(), equalTo(userAddDto1.getName()));
        assertThat(savedUser.getEmail(), equalTo(userAddDto1.getEmail()));

    }

    @Test
    void getAllUsers() {
        userRepository.save(UserMapper.toUser(userAddDto1));
        userRepository.save(UserMapper.toUser(userAddDto2));
        List<User> users = userRepository.findAll();
        assertThat(users,hasSize(2));
        assertThat(users.get(0).getName(), equalTo(userAddDto1.getName()));

    }
}
