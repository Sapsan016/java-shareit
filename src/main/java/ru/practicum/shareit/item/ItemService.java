package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getItems(long userId);

    Item addItem(Item item, long userId);


    Item updateItem(long itemId, long userId, Item item);

    Item getItemById(long itemId);

    List<Item> searchItems(String text);

    Comment addComment(Comment comment, long userId, long itemId);
}

