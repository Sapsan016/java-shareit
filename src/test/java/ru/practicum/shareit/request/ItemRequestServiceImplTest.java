package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemAddDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserAddDto;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemRequestServiceImplTest {
    long ID = 1L;

    ItemRequestService itemRequestService;

    ItemService itemService;

    UserService userService;

    ItemRequestAddDto itemRequestAddDto1 = new ItemRequestAddDto("Test description 1",
            null, null);

    ItemRequestAddDto itemRequestAddDto2 = new ItemRequestAddDto("Test description 2",
            null, null);
    User user = new User(ID, "testUser1", "user1@email.com");

    UserAddDto userAddDto1 = new UserAddDto("testUser1", "user1@email.com");
    UserAddDto userAddDto2 = new UserAddDto("testUser2", "user2@email.com");


    @Test
    void addRequest() {
        User savedUser = userService.addUser(userAddDto1);
        ItemRequest savedRequest = itemRequestService.addRequest(itemRequestAddDto1, savedUser.getId());
        assertThat(savedRequest.getId(), equalTo(ID));
        assertThat(savedRequest.getRequester(), equalTo(savedUser));
        assertThat(savedRequest.getDescription(), equalTo(itemRequestAddDto1.getDescription()));
    }

    @Test
    void getRequestById() {
        User savedUser = userService.addUser(userAddDto1);
        ItemRequest savedRequest = itemRequestService.addRequest(itemRequestAddDto1, savedUser.getId());
        ItemAddDto item = new ItemAddDto("Item1", "item1 description", true, savedRequest.getId());
        itemService.addItem(item, savedUser.getId());
        ItemRequestDto requestDto = itemRequestService.getRequestById(savedRequest.getId(), savedUser.getId());
        assertThat(requestDto.getId(), equalTo(savedRequest.getId()));
        assertThat(requestDto.getRequester(), equalTo(savedRequest.getRequester()));
        assertThat(requestDto.getDescription(), equalTo(savedRequest.getDescription()));
        assertThat(requestDto.getItems().size(), equalTo(1));
    }

    @Test
    void getRequestForUser() {
        User savedUser = userService.addUser(userAddDto1);
        ItemRequest savedRequest = itemRequestService.addRequest(itemRequestAddDto1, savedUser.getId());
        ItemAddDto item = new ItemAddDto("Item1", "item1 description", true, savedRequest.getId());
        Item savedItem = itemService.addItem(item, savedUser.getId());
        List<ItemRequestDto> requestDtoList = itemRequestService.getRequestsForUser(savedUser.getId());
        assertThat(requestDtoList.size(), equalTo(1));
        assertThat(requestDtoList.get(0).getRequester(), equalTo(savedRequest.getRequester()));
        assertThat(requestDtoList.get(0).getItems().get(0), equalTo(savedItem));
    }

    @Test
    void getAllRequestsWithParam() {
        User savedUser = userService.addUser(userAddDto1);
        User savedUser2 = userService.addUser(userAddDto2);
        ItemRequest savedRequest = itemRequestService.addRequest(itemRequestAddDto1, savedUser.getId());
        ItemAddDto item = new ItemAddDto("Item1", "item1 description", true, savedRequest.getId());
        Item savedItem = itemService.addItem(item, savedUser.getId());
        ItemAddDto item2 = new ItemAddDto("Item2", "item2 description", true, savedRequest.getId());
        itemService.addItem(item2, savedUser.getId());
        List<ItemRequestDto> requestDtoList = itemRequestService.getAllRequestsWithParam(savedUser2.getId(),
                0, 1);
        assertThat(requestDtoList.size(), equalTo(1));
        assertThat(requestDtoList.get(0).getItems().get(0), equalTo(savedItem));
    }

    @Test
    void getAllRequests() {
        User savedUser = userService.addUser(userAddDto1);
        User savedUser2 = userService.addUser(userAddDto2);
        ItemRequest savedRequest = itemRequestService.addRequest(itemRequestAddDto1, savedUser.getId());
        ItemRequest savedRequest2 = itemRequestService.addRequest(itemRequestAddDto2, savedUser.getId());
        ItemAddDto item = new ItemAddDto("Item1", "item1 description", true, savedRequest.getId());
        Item savedItem = itemService.addItem(item, savedUser.getId());
        ItemAddDto item2 = new ItemAddDto("Item2", "item2 description", true, savedRequest2.getId());
        Item savedItem2 = itemService.addItem(item2, savedUser.getId());
        List<ItemRequestDto> requestDtoList = itemRequestService.getAllRequests(savedUser2.getId());
        assertThat(requestDtoList.size(), equalTo(2));
        assertThat(requestDtoList.get(0).getItems().get(0), equalTo(savedItem2));
        assertThat(requestDtoList.get(1).getItems().get(0), equalTo(savedItem));
    }

    @Test
    void failedGetRequest() {
        User savedUser = userService.addUser(userAddDto1);
        try {
            itemRequestService.getRequestById(99L, savedUser.getId());
        } catch (ItemRequestNotFoundException e) {
            assertThat(("Запрос с id " + 99 + " не найден"), equalTo(e.getMessage()));
        }
    }

    @Test
    void failedGetAllRequestsWithParam() {
        User savedUser = userService.addUser(userAddDto1);
        try {
            itemRequestService.getAllRequestsWithParam(savedUser.getId(), -1, 0);
        } catch (InvalidDataException e) {
            assertThat(("Неверные параметры"), equalTo(e.getMessage()));
        }
    }
}
