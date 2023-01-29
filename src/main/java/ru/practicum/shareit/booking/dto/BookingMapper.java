package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;


public class BookingMapper {

    public static Booking toBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                bookingDto.getItem()!= null ? bookingDto.getItem() : null,
                bookingDto.getBooker()!= null ? bookingDto.getBooker() : null,
                bookingDto.getStatus()
                );


    }
    public static Booking fromRequest(BookingRequestDto bookingRequestDto) {
        return new Booking(
                bookingRequestDto.getId(),
                bookingRequestDto.getStart(),
                bookingRequestDto.getEnd(),
                null,
                null,
                null
        );
    }

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getBooker(),
                booking.getItem(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }
}