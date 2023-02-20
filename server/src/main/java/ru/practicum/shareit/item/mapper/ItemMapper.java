package ru.practicum.shareit.item.mapper;


import ru.practicum.shareit.item.ItemAddDto;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;

public class ItemMapper {
    public static Item toItem(ItemAddDto itemAddDto) {
        return new Item(
                null,
                itemAddDto.getName(),
                itemAddDto.getDescription(),
                itemAddDto.getAvailable(),
                null,
                itemAddDto.getRequestId()
        );
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwnerId(),
                item.getRequestId() != null ? item.getRequestId() : null,
                null,
                null,
                new ArrayList<>()
        );
    }
}
