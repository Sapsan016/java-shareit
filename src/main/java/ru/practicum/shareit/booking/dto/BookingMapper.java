package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.ItemRepository;

@Component
public class BookingMapper {


    private final ItemRepository itemRepository;

    public BookingMapper(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus(),
                booking.getBookerId(),
                booking.getItemId(),
                itemRepository.findById(booking.getItemId()).orElseThrow().getName()
        );
    }
}
