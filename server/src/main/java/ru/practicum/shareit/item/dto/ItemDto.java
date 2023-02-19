package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.BookingDto;

import java.util.List;


@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    long id;
    String name;
    String description;
    Boolean available;
    Long owner;
    Long requestId;
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDTO> comments;
}
