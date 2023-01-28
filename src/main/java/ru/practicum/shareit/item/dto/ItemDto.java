package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;


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
