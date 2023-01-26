package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

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
    LocalDateTime lastBooking;
    LocalDateTime nextBooking;




}
