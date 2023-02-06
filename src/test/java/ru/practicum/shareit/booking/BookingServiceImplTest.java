package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;
@Transactional
@SpringBootTest(
     //  properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class BookingServiceImplTest {

    BookingRepository bookingRepository;
    BookingServiceImpl bookingService;

    UserRepository userRepository;

    ItemRepository itemRepository;

    Item item = new Item(0,"Item1","test item1",true,1L,null);
    User user = new User(0,"User1","user1@mail.com");

    Booking booking = new Booking(0, LocalDateTime.now(),LocalDateTime.now().plusMinutes(5),item,user,BookingStatus.WAITING);

    @Test
    void addBooking() {
        userRepository.save(user);
        itemRepository.save(item);
        Booking savedBooking = bookingRepository.save(booking);
        assertThat(savedBooking.getId(),notNullValue());
        assertThat(savedBooking.getItem(),equalTo(item));


    }

    @Test
    void approveBooking() {
    }

    @Test
    void getBookingById() {
    }

    @Test
    void getUserBooking() {
    }

    @Test
    void getUserItemBooking() {
    }
    @Test
    void validateDateTimeBooking() {


        }

    }
