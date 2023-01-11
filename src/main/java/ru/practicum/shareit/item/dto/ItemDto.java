package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor


@FieldDefaults(level= AccessLevel.PRIVATE)
public class ItemDto {
    @NonFinal
    long id;
    String name;
    String description;
    boolean available;
    User owner;
    ItemRequest request;


}
