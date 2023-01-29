package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.Booking;


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
    Booking lastBooking;
    Booking nextBooking;


}
