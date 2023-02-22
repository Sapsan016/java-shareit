package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingAddDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserAddDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
     //   properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class ItemServiceImplTest {
    ItemService itemService;
    UserService userService;
    BookingService bookingService;
    UserAddDto userAddDto1 = new UserAddDto("testUser1", "user1@email.com");
    UserAddDto userAddDto2 = new UserAddDto("testUser2", "user2@email.com");
    ItemAddDto itemAddDto1 = new ItemAddDto("Test item1", "Test item description 1",
            true, null);
    ItemAddDto itemAddDto2 = new ItemAddDto("Test item2", "Test item description 2",
            true, null);
    ItemAddDto itemAddDtoUpdate = new ItemAddDto("Update item1", "Update description 1",
            false, null);
    ItemAddDto itemAddDtoFailed = new ItemAddDto("Add item1", "failed",
            null, null);

    @NonFinal
    User owner;
    @NonFinal
    Item savedItem;
    @NonFinal
    Item savedItem2;
    @NonFinal
    User booker;
    @NonFinal
    Booking longBooking;
    @NonFinal
    Booking shortBooking;

    @BeforeEach
    void setUp() {
        owner = userService.addUser(userAddDto1);
        savedItem = itemService.addItem(itemAddDto1, owner.getId());
        savedItem2 = itemService.addItem(itemAddDto2, owner.getId());
        booker = userService.addUser(userAddDto2);
        BookingAddDto bookingAddDto = new BookingAddDto(savedItem.getId(), LocalDateTime.now().plusMinutes(5),
                LocalDateTime.now().plusMinutes(60));
        BookingAddDto bookingAddDto2 = new BookingAddDto(savedItem.getId(), LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(1));
        longBooking = bookingService.addBooking(bookingAddDto, booker.getId());
        shortBooking = bookingService.addBooking(bookingAddDto2, booker.getId());
    }

    @Test
    void addItem() {
        assertThat(savedItem.getId(), notNullValue());
        assertThat(savedItem.getName(), equalTo(itemAddDto1.getName()));
        assertThat(savedItem.getDescription(), equalTo(itemAddDto1.getDescription()));
        assertThat(savedItem.getOwnerId(), equalTo(owner.getId()));
    }

    @Test
    void getItemByIdWithComment() throws InterruptedException {
        bookingService.approveBooking(shortBooking.getId(), owner.getId(), true);
        Thread.sleep(2000);
        CommentAddDto commentAddDto = new CommentAddDto("Test comment", 0, 0, null);

        Comment comment = itemService.addComment(commentAddDto, booker.getId(), savedItem.getId());
        ItemDto returnedItem = itemService.getItemById(savedItem.getId(), owner.getId());
        assertThat(returnedItem.getId(), equalTo(savedItem.getId()));
        assertThat(returnedItem.getName(), equalTo(itemAddDto1.getName()));
        assertThat(returnedItem.getDescription(), equalTo(itemAddDto1.getDescription()));
        assertThat(returnedItem.getOwner(), equalTo(owner.getId()));
        assertThat(returnedItem.getNextBooking(), equalTo(BookingMapper.toBookingDto(longBooking)));
        assertThat(returnedItem.getLastBooking(), equalTo(BookingMapper.toBookingDto(shortBooking)));
        assertThat(returnedItem.getComments(), equalTo(List.of(CommentMapper.toCommentDto(comment))));
    }

    @Test
    void getItems() {
        List<ItemDto> itemDtoList = itemService.getItems(owner.getId());
        assertThat(itemDtoList.size(), equalTo(2));
        assertThat(itemDtoList.get(0).getLastBooking(), equalTo(BookingMapper.toBookingDto(shortBooking)));
        assertThat(itemDtoList.get(1), equalTo(ItemMapper.toItemDto(savedItem2)));
    }

    @Test
    void getItemsWithParam() {
        List<ItemDto> itemDtoParamList = itemService.getItemsWithParam(owner.getId(), 1L, 1L);
        assertThat(itemDtoParamList.size(), equalTo(1));
        assertThat(itemDtoParamList.get(0), equalTo(ItemMapper.toItemDto(savedItem2)));
        assertThat(itemDtoParamList.get(0).getNextBooking(), nullValue());
    }

    @Test
    void updateItem() {
        Item updatedItem = itemService.updateItem(savedItem.getId(), owner.getId(), itemAddDtoUpdate);
        assertThat(updatedItem.getName(), equalTo(updatedItem.getName()));
        assertThat(updatedItem.getDescription(), equalTo(updatedItem.getDescription()));
        assertThat(updatedItem.getAvailable(), equalTo(false));
    }

    @Test
    void searchItems() {
        List<Item> items = itemService.searchItems("IteM");
        assertThat(items.size(), equalTo(2));
        assertThat(items.get(1), equalTo(savedItem2));
    }

    @Test
    void searchItemsWithParams() {
        List<Item> items = itemService.searchItemsWithParams("IteM", 1L, 1L);
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0), equalTo(savedItem2));
    }

    @Test
    void failedGetItemsWrongUser() {
        try {
            itemService.getItems(99L);
        } catch (UserNotFoundException e) {
            assertThat("Пользователь не является хозяином вещей", equalTo(e.getMessage()));
        }
    }

    @Test
    void failedUpdateItemWrongItem() {
        try {
            itemService.updateItem(99L, owner.getId(), itemAddDto1);
        } catch (ItemNotFoundException e) {
            assertThat((String.format("Вещь с id %s не найдена", 99L)), equalTo(e.getMessage()));
        }
    }

    @Test
    void failedUpdateItemWrongUser() {
        try {
            itemService.updateItem(savedItem.getId(), 99L, itemAddDto1);
        } catch (UserNotFoundException e) {
            assertThat("Владелец не найден", equalTo(e.getMessage()));
        }
    }

    @Test
    void failedAddItemWrongUser() {
        try {
            itemService.addItem(itemAddDto1, 99L);
        } catch (UserNotFoundException e) {
            assertThat("Пользователь не найден", equalTo(e.getMessage()));
        }
    }

    @Test
    void returnItemWithoutBookingForWrongOwner() {
        ItemDto itemDto = itemService.getItemById(savedItem.getId(), booker.getId());
        assertThat(itemDto.getNextBooking(), nullValue());
    }

    @Test
    void returnItemWithoutBooking() {
        ItemDto itemDto = itemService.getItemById(savedItem2.getId(), owner.getId());
        assertThat(itemDto.getNextBooking(), nullValue());
    }

    @Test
    void failedAddCommentWrongBooking() {
        try {
            itemService.addComment(new CommentAddDto("Test comment", 0, 0,
                    null), booker.getId(), 99L);
        } catch (UnavailableException e) {
            assertThat("Невозможно добавить отзыв. Проверьте данные бронирования", equalTo(e.getMessage()));
        }
    }
}