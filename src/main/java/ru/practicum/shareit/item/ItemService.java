package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public interface ItemService {
    ItemDto addItem(Item item, long userId);

    ItemDto updateItem(long itemId, long userId, Item item);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getItems(long userId);

    List<ItemDto> searchItems(long userId, String text);
}
