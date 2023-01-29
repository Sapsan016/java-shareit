package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

@Component
public class BookingMapper {


    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingMapper(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                userRepository.findById(booking.getBookerId()).orElseThrow(),
                itemRepository.findById(booking.getItemId()).orElseThrow(),
                booking.getStart(),
                booking.getEnd(),
                booking.getStatus()
        );
    }
}