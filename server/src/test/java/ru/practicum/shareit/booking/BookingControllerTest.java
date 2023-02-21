package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class BookingControllerTest {
    @Autowired
    @NonFinal
    ObjectMapper mapper;
    @MockBean
    @NonFinal
    BookingServiceImpl bookingService;
    @Autowired
    @NonFinal
    MockMvc mvc;

    static String HEADER = "X-Sharer-User-Id";
    static long ID = 1L;
    static LocalDateTime START = LocalDateTime.now();
    static LocalDateTime END = LocalDateTime.now().plusSeconds(1);

    User booker = new User(ID, "John", "john.doe@mail.com");
    Item item1 = new Item(ID, "Item1", "Item1 description", true, ID, null);
    BookingAddDto bookingAddDto = new BookingAddDto(ID, START, END);
    Booking booking = new Booking(ID, START, END, item1, booker, BookingStatus.WAITING);

    Booking booking2 = new Booking(2L, START, END, item1, booker, BookingStatus.WAITING);


    @Test
    void addNewBooking() throws Exception {
        when(bookingService.addBooking(bookingAddDto, ID))
                .thenReturn(booking);
        String expectedResponse = mapper.writeValueAsString(booking);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingAddDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void approveBooking() throws Exception {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingService.approveBooking(ID, ID, true))
                .thenReturn(booking);
        String expectedResponse = mapper.writeValueAsString(booking);
        mvc.perform(patch("/bookings/1?approved=true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBookingById(ID, ID))
                .thenReturn(booking);
        String expectedResponse = mapper.writeValueAsString(booking);
        mvc.perform(get("/bookings/1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getUserBookingWithParams() throws Exception {
        when(bookingService.getUserBooking("ALL", ID))
                .thenReturn(List.of(booking, booking2));
        String expectedResponse = mapper.writeValueAsString(List.of(booking2));
        mvc.perform(get("/bookings?state=ALL&from=1&size=1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getOwnerBookingWithoutParams() throws Exception {
        when(bookingService.getUserItemBooking("ALL", ID))
                .thenReturn(List.of(booking, booking2));
        String expectedResponse = mapper.writeValueAsString(List.of(booking, booking2));
        mvc.perform(get("/bookings/owner?state=ALL")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getOwnerBookingWithParams() throws Exception {
        when(bookingService.getUserItemBooking("ALL", ID))
                .thenReturn(List.of(booking, booking2));
        String expectedResponse = mapper.writeValueAsString(List.of(booking2));
        mvc.perform(get("/bookings/owner?state=ALL&from=1&size=1")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, ID))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponse));
    }

    @Test
    void getOwnerBookingWithWrongParams() throws Exception {
        when(bookingService.getUserBooking("ALL", ID))
                .thenReturn(List.of(booking, booking2));
        mvc.perform(get("/bookings/owner?state=ALL&from=0&size=0")
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HEADER, ID))
                .andExpect(status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException()
                        instanceof InvalidDataException));
    }

}