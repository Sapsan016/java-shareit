package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingAddDto;
import ru.practicum.shareit.booking.BookingDto;

public class BookingMapper {

    public static Booking toBooking(BookingAddDto bookingAddDto) {
        return new Booking(
                0,
                bookingAddDto.getStart(),
                bookingAddDto.getEnd(),
                null,
                null,
                null
        );
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getBooker().getId(),
                booking.getBooker(),
                booking.getItem(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }
}