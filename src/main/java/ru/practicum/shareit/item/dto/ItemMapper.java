package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    private final BookingRepository bookingRepository;

    public ItemMapper(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getRequestId() != null ? item.getRequestId() : null,
                item.getLastBookingId() != 0 ? bookingRepository.findById(item.getLastBookingId()).get() : null,
                item.getNextBookingId() != 0 ? bookingRepository.findById(item.getNextBookingId()).get() : null
        );
    }
}
