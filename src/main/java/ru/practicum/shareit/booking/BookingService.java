package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {
    Booking addBooking(BookingRequestDto booking, long userId);

    Booking approveBooking(long bookingId, long userId, boolean approved);


    Booking getBookingById(long bookingId, long userId);


    List<Booking> getUserBooking(String state, long userId);

    List<Booking> getUserItemBooking(String state, long userId);

}
