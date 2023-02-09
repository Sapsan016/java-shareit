package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemAddDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserAddDto;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class BookingServiceImplTest {

    BookingService bookingService;
    ItemService itemService;
    UserService userService;
    UserAddDto userAddDto1 = new UserAddDto("testUser1", "user1@email.com");
    UserAddDto userAddDto2 = new UserAddDto("testUser2", "user2@yandex.ru");
    ItemAddDto itemAddDto = new ItemAddDto("Item1", "test item1", true, null);
    ItemAddDto itemAddDto2 = new ItemAddDto("Item2", "test item2", true, null);

    LocalDateTime start = LocalDateTime.now().plusMinutes(1);

    LocalDateTime end = LocalDateTime.now().plusMinutes(60);
    @NonFinal
    User owner;
    @NonFinal
    User booker;
    @NonFinal
    Item savedItem;
    @NonFinal
    BookingAddDto bookingAddDto;
    @NonFinal
    Booking savedBooking;
    @NonFinal
    Booking savedBooking2;

    @BeforeEach
    void setUp() {
        owner = userService.addUser(userAddDto1);
        booker = userService.addUser(userAddDto2);
        savedItem = itemService.addItem(itemAddDto, owner.getId());
        bookingAddDto = new BookingAddDto(savedItem.getId(), start, end);
        savedBooking = bookingService.addBooking(bookingAddDto, booker.getId());
    }

    void addSecondBooking() {
        Item savedItem2 = itemService.addItem(itemAddDto2, owner.getId());
        BookingAddDto bookingAddDto2 = new BookingAddDto(savedItem2.getId(), start, end);
        savedBooking2 = bookingService.addBooking(bookingAddDto2, booker.getId());
    }

    @Test
    void addBooking() {
        assertThat(savedBooking.getId(), notNullValue());
        assertThat(savedBooking.getItem(), equalTo(savedItem));
        assertThat(savedBooking.getStatus(), equalTo(BookingStatus.WAITING));
        assertThat(savedBooking.getStart(), equalTo(start));
        assertThat(savedBooking.getEnd(), equalTo(end));
        assertThat(savedBooking.getBooker(), equalTo(booker));
    }

    @Test
    void approveBooking() {
        addSecondBooking();
        bookingService.approveBooking(savedBooking.getId(), owner.getId(), true);
        assertThat(savedBooking.getStatus(), equalTo(BookingStatus.APPROVED));
        bookingService.approveBooking(savedBooking2.getId(), owner.getId(), false);
        assertThat(savedBooking2.getStatus(), equalTo(BookingStatus.REJECTED));
    }

    @Test
    void getBookingById() {
        Booking booking = bookingService.getBookingById(savedBooking.getId(), booker.getId());
        assertThat(booking.getId(), equalTo(savedBooking.getId()));
        assertThat(booking.getBooker(), equalTo(booker));
        assertThat(booking.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void getUserBookingAll() {
        addSecondBooking();
        List<Booking> allBookings = bookingService.getUserBooking("ALL", booker.getId());
        assertThat(allBookings.size(), equalTo(2));
        assertThat(allBookings.get(0), equalTo(savedBooking));
    }

    @Test
    void getUserBookingPast() throws InterruptedException {
        BookingAddDto bookingAddDto3 = new BookingAddDto(savedItem.getId(), LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2));
        Booking savedPastBooking = bookingService.addBooking(bookingAddDto3, booker.getId());
        Thread.sleep(2000);
        List<Booking> pastBookings = bookingService.getUserBooking("PAST", booker.getId());
        assertThat(pastBookings.size(), equalTo(1));
        assertThat(pastBookings.get(0), equalTo(savedPastBooking));
    }

    @Test
    void getUserBookingFuture() {
        List<Booking> futureBooking = bookingService.getUserBooking("FUTURE", booker.getId());
        assertThat(futureBooking.size(), equalTo(1));
        assertThat(futureBooking.get(0), equalTo(savedBooking));
    }

    @Test
    void getUserBookingCurrent() throws InterruptedException {
        BookingAddDto bookingAddDto4 = new BookingAddDto(savedItem.getId(), LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(30));
        Booking savedCurrentBooking = bookingService.addBooking(bookingAddDto4, booker.getId());
        Thread.sleep(1500);
        List<Booking> currentBookings = bookingService.getUserBooking("CURRENT", booker.getId());
        assertThat(currentBookings.size(), equalTo(1));
        assertThat(currentBookings.get(0), equalTo(savedCurrentBooking));
    }

    @Test
    void getUserBookingWaiting() {
        List<Booking> waitingBookings = bookingService.getUserBooking("WAITING", booker.getId());
        assertThat(waitingBookings.size(), equalTo(1));
        assertThat(waitingBookings.get(0), equalTo(savedBooking));
    }

    @Test
    void getUserBookingRejected() {
        bookingService.approveBooking(savedBooking.getId(), owner.getId(), false);
        List<Booking> rejectedBookings = bookingService.getUserBooking("REJECTED", booker.getId());
        assertThat(rejectedBookings.size(), equalTo(1));
        assertThat(rejectedBookings.get(0), equalTo(savedBooking));
    }

    @Test
    void getUserItemBookingAll() {
        addSecondBooking();
        List<Booking> allItemBookings = bookingService.getUserItemBooking("ALL", owner.getId());
        assertThat(allItemBookings.size(), equalTo(2));
        assertThat(allItemBookings.get(0), equalTo(savedBooking));
    }

    @Test
    void getUserItemBookingPast() throws InterruptedException {
        BookingAddDto bookingAddDto5 = new BookingAddDto(savedItem.getId(), LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(2));
        Booking savedPastItemBooking = bookingService.addBooking(bookingAddDto5, booker.getId());
        Thread.sleep(2000);
        List<Booking> pastBookings = bookingService.getUserItemBooking("PAST", owner.getId());
        assertThat(pastBookings.size(), equalTo(1));
        assertThat(pastBookings.get(0), equalTo(savedPastItemBooking));
    }

    @Test
    void getUserItemBookingFuture() {
        List<Booking> futureItemBooking = bookingService.getUserItemBooking("FUTURE", owner.getId());
        assertThat(futureItemBooking.size(), equalTo(1));
        assertThat(futureItemBooking.get(0), equalTo(savedBooking));
    }

    @Test
    void getUserItemBookingCurrent() throws InterruptedException {
        BookingAddDto bookingAddDto6 = new BookingAddDto(savedItem.getId(), LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(30));
        Booking savedCurrentBooking = bookingService.addBooking(bookingAddDto6, booker.getId());
        Thread.sleep(1500);
        List<Booking> currentBookings = bookingService.getUserItemBooking("CURRENT", owner.getId());
        assertThat(currentBookings.size(), equalTo(1));
        assertThat(currentBookings.get(0), equalTo(savedCurrentBooking));
    }

    @Test
    void getUserItemBookingWaiting() {
        List<Booking> waitingItemBookings = bookingService.getUserItemBooking("WAITING", owner.getId());
        assertThat(waitingItemBookings.size(), equalTo(1));
        assertThat(waitingItemBookings.get(0), equalTo(savedBooking));
    }

    @Test
    void getUserItemBookingRejected() {
        bookingService.approveBooking(savedBooking.getId(), owner.getId(), false);
        List<Booking> rejectedItemBookings = bookingService.getUserItemBooking("REJECTED", owner.getId());
        assertThat(rejectedItemBookings.size(), equalTo(1));
        assertThat(rejectedItemBookings.get(0), equalTo(savedBooking));
    }

    @Test
    void failedAddBookingUser() {
        try {
            bookingService.addBooking(new BookingAddDto(savedItem.getId(), start, end), 99L);
        } catch (UserNotFoundException e) {
            assertThat(("Пользователь с id " + 99 + " не найден"), equalTo(e.getMessage()));
        }
    }

    @Test
    void failedAddBookingItem() {
        try {
            bookingService.addBooking(new BookingAddDto(99L, start, end), booker.getId());
        } catch (ItemNotFoundException e) {
            assertThat(("Вещь с id " + 99 + " не найдена"), equalTo(e.getMessage()));
        }
    }

    @Test
    void failedValidateBookingOwner() {
        try {
            bookingService.addBooking(new BookingAddDto(savedItem.getId(), start, end), owner.getId());
        } catch (UserNotFoundException e) {
            assertThat(("Владелец не может бронировать свои вещи."), equalTo(e.getMessage()));
        }
    }

    @Test
    void failedValidateBookingItemUnavailable() {
        savedItem.setAvailable(false);
        try {
            bookingService.addBooking(new BookingAddDto(savedItem.getId(), start, end), booker.getId());
        } catch (UnavailiableException e) {
            assertThat(("Вещь с id " + savedItem.getId() + " не доступна для бронирования."),
                    equalTo(e.getMessage()));
        }
    }

    @Test
    void failedValidateBookingDate() {
        try {
            bookingService.addBooking(new BookingAddDto(savedItem.getId(), start.minusDays(1), end), booker.getId());
        } catch (InvalidDataException e) {
            assertThat(("Неправильное время начала или конца бронирования."),
                    equalTo(e.getMessage()));
        }
    }

    @Test
    void failedApproveBookingNotFound() {
        try {
            bookingService.approveBooking(99L, owner.getId(), true);
        } catch (BookingNotFoundException e) {
            assertThat(("Бронирование с id " + 99 + " не найдено"), equalTo(e.getMessage()));
        }
    }

    @Test
    void failedApproveBookingAlreadyApproved() {
        bookingService.approveBooking(savedBooking.getId(), owner.getId(), true);
        try {
            bookingService.approveBooking(savedBooking.getId(), owner.getId(), true);
        } catch (UnavailiableException e) {
            assertThat(("Бронирование уже подтверждено"), equalTo(e.getMessage()));
        }
    }

    @Test
    void failedApproveBookingWrongOwner() {
        try {
            bookingService.approveBooking(savedBooking.getId(), booker.getId(), true);
        } catch (UserNotFoundException e) {
            assertThat(("Пользователь с id " + booker.getId() + " не является владельцем вещи.")
                    , equalTo(e.getMessage()));
        }
    }

    @Test
    void unsupportedParameter() {
        try {
            bookingService.getUserBooking("WTF", booker.getId());
        } catch (UnavailiableException e) {
            assertThat(("Unknown state: UNSUPPORTED_STATUS"), equalTo(e.getMessage()));
        }
    }
}