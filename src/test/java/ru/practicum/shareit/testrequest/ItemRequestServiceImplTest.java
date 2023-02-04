package ru.practicum.shareit.testrequest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserAddDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestServiceImplTest {

    ItemRequestRepository requestRepository;
    UserRepository userRepository;

    long ID = 1L;


    ItemRequestAddDto itemRequestAddDto1 = new ItemRequestAddDto("Test description 1",
            null, null);

    ItemRequestAddDto itemRequestAddDto2 = new ItemRequestAddDto("Test description 2",
            null, null);
    UserAddDto userAddDto1 = new UserAddDto("testUser1", "user1@email.com");

    @Test
    void addRequest() {
        userRepository.save(UserMapper.toUser(userAddDto1));
        ItemRequest requestToSave = ItemRequestMapper.toItemRequest(itemRequestAddDto1);
        User requester = userRepository.findById(ID).orElseThrow(() ->
                new UserNotFoundException("User not found"));
        requestToSave.setRequester(requester);
        requestRepository.save(requestToSave);
        ItemRequest savedRequest = requestRepository.findById(ID).orElseThrow(() ->
                new ItemRequestNotFoundException("Request not found"));
        assertThat(savedRequest.getId(), notNullValue());
        assertThat(savedRequest.getDescription(), equalTo(itemRequestAddDto1.getDescription()));
        assertThat(savedRequest.getRequester(), equalTo(requester));
    }

}
