package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;


@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {

    long id;
    String name;
    String description;
    boolean available;
    Long owner;
    Long request;
    BookingDto lastBooking;
    BookingDto nextBooking;
    List<CommentDTO> comments;


}
