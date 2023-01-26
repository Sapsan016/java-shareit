package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingServiceImpl bookingServiceImpl;
    private final BookingMapper bookingMapper;
    static final String HEADER = "X-Sharer-User-Id";

    public BookingController(BookingServiceImpl bookingServiceImpl, BookingMapper bookingMapper) {
        this.bookingServiceImpl = bookingServiceImpl;
        this.bookingMapper = bookingMapper;
    }

    @PostMapping
    public BookingDto addBooking(@RequestBody Booking booking,                            //Создание бронирования
                              @RequestHeader(HEADER) long userId) {
        //System.out.println(bookingMapper.bookingDto(bookingServiceImpl.addBooking(booking, userId)));
        return bookingMapper.toBookingDto(bookingServiceImpl.addBooking(booking, userId));

    }

    @PatchMapping("/{bookingId}")      //Подтверждение или отклонение бонирования
    public BookingDto updateItem(@PathVariable long bookingId, @RequestHeader(HEADER) long userId,
                           @RequestParam("approved") boolean approved) {
        return bookingMapper.toBookingDto(bookingServiceImpl.approveBooking(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")        //Получение данных о конкретном бронировании
    public BookingDto getBookingById(@PathVariable long bookingId, @RequestHeader(HEADER) long userId) {
        return bookingMapper.toBookingDto(bookingServiceImpl.getBookingById(bookingId,userId));

    }
    @GetMapping()        //Получение списка всех бронирований текущего пользователя
    public List<BookingDto> getUserBooking(@RequestParam(defaultValue = "ALL") String state,
                                           @RequestHeader(HEADER) long userId) {
        return bookingServiceImpl.getUserBooking(state, userId).stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
    @GetMapping("/owner")        //Получение списка бронирований для всех вещей текущего пользователя
    public List<BookingDto> getUserItemBooking(@RequestParam(defaultValue = "ALL") String state,
                                     @RequestHeader(HEADER) long userId) {
        return bookingServiceImpl.getUserItemBooking(state, userId).stream()
                .map(bookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

}
