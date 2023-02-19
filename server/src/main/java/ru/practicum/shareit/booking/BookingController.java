package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.InvalidDataException;


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
    public BookingDto addBooking(@RequestBody BookingAddDto bookingAddDto,            //Добавление бронирования
                                 @RequestHeader(HEADER) long userId) {
        return BookingMapper.toBookingDto(bookingService.addBooking(bookingAddDto, userId));
    }

    @PatchMapping("/{bookingId}")                                          //Подтверждение или отклонение бонирования
    public BookingDto approveBooking(@PathVariable long bookingId, @RequestHeader(HEADER) long userId,
                                     @RequestParam("approved") boolean approved) {
        return BookingMapper.toBookingDto(bookingService.approveBooking(bookingId, userId, approved));
    }

    @GetMapping("/{bookingId}")                                          //Получение данных о конкретном бронировании
    public BookingDto getBookingById(@PathVariable long bookingId, @RequestHeader(HEADER) long userId) {
        return BookingMapper.toBookingDto(bookingService.getBookingById(bookingId, userId));
    }

    @GetMapping()                                          //Получение списка всех бронирований текущего пользователя
    public List<BookingDto> getUserBooking(@RequestParam(defaultValue = "ALL") String state,
                                           @RequestHeader(HEADER) long userId,
                                           @RequestParam(required = false) Long from,
                                           @RequestParam(required = false) Long size) {
        if (from == null || size == null) {
            return bookingService.getUserBooking(state, userId).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (from < 0 || size <= 0)
            throw new InvalidDataException("Неверные параметры");
        return bookingService.getUserBooking(state, userId).stream()
                .map(BookingMapper::toBookingDto)
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    @GetMapping("/owner")        //Получение списка бронирований для всех вещей текущего пользователя
    public List<BookingDto> getUserItemBooking(@RequestParam(defaultValue = "ALL") String state,
                                               @RequestHeader(HEADER) long userId,
                                               @RequestParam(required = false) Long from,
                                               @RequestParam(required = false) Long size) {
        if (from == null || size == null) {
            return bookingService.getUserItemBooking(state, userId).stream()
                    .map(BookingMapper::toBookingDto)
                    .collect(Collectors.toList());
        }
        if (from < 0 || size <= 0)
            throw new InvalidDataException("Неверные параметры");
        return bookingService.getUserItemBooking(state, userId).stream()
                .map(BookingMapper::toBookingDto)
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }
}