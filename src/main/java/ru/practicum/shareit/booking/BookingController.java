package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;


import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    static final String HEADER = "X-Sharer-User-Id";

    public BookingController(BookingServiceImpl bookingServiceImpl) {
        this.bookingService = bookingServiceImpl;

    }

    @PostMapping
    public BookingDto addBooking(@RequestBody BookingRequestDto bookingRequestDto,               //Создание бронирования
                                 @RequestHeader(HEADER) long userId) {
        return BookingMapper.toBookingDto(bookingService.addBooking(bookingRequestDto, userId));

    }

    @PatchMapping("/{bookingId}")      //Подтверждение или отклонение бонирования
    public BookingDto updateItem(@PathVariable long bookingId, @RequestHeader(HEADER) long userId,
                                 @RequestParam("approved") boolean approved) {
        return BookingMapper.toBookingDto(bookingService.approveBooking(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")        //Получение данных о конкретном бронировании
    public BookingDto getBookingById(@PathVariable long bookingId, @RequestHeader(HEADER) long userId) {
        return BookingMapper.toBookingDto(bookingService.getBookingById(bookingId, userId));

    }

    @GetMapping()        //Получение списка всех бронирований текущего пользователя
    public List<BookingDto> getUserBooking(@RequestParam(defaultValue = "ALL") String state,
                                           @RequestHeader(HEADER) long userId) {
        return bookingService.getUserBooking(state, userId).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")        //Получение списка бронирований для всех вещей текущего пользователя
    public List<BookingDto> getUserItemBooking(@RequestParam(defaultValue = "ALL") String state,
                                               @RequestHeader(HEADER) long userId) {
        return bookingService.getUserItemBooking(state, userId).stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
