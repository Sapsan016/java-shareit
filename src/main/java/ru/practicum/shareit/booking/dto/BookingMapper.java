package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.ItemService;

@Component
public class BookingMapper  {

    private final ItemService itemService;

    public BookingMapper(ItemService itemService) {
        this.itemService = itemService;
    }

    public BookingDto toBookingDto(Booking booking) {
    return new BookingDto(
            booking.getId(),
            booking.getStart(),
            booking.getEnd(),
            booking.getStatus(),
            booking.getBookerId(),
            booking.getItemId(),
            itemService.getItemById(booking.getItemId()).getName()
    );
}
}
