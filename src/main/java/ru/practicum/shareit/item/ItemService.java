package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
public interface ItemService {
    Item addItem(ItemDto itemDto, long userId);

    Item updateItem(long itemId, long userId);

    Item getItemById(long itemId, long userId);

    List<Item> getItems(long userId);

    List<Item> searchItems(long userId);
}
