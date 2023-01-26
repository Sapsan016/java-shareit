package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getRequestId() != null ? item.getRequestId() : null,
                item.getLastBooking() != null ? item.getLastBooking() : null,
                item.getNextBooking() != null ? item.getNextBooking() : null
        );
    }
}
