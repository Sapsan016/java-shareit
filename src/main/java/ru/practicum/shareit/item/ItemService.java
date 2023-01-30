package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(long userId);

    Item addItem(Item item, long userId);


    Item updateItem(long itemId, long userId, Item item);

    ItemDto getItemById(long itemId, long userId);

    List<Item> searchItems(String text);

    Comment addComment(Comment comment, long userId, long itemId);
}

