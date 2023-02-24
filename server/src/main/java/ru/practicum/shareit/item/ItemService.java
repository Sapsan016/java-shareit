package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(long userId);

    Item addItem(ItemAddDto itemAddDto, long userId);


    Item updateItem(long itemId, long userId, ItemAddDto item);

    ItemDto getItemById(long itemId, long userId);

    List<Item> searchItems(String text);

    List<Item> searchItemsWithParams(String text, Long from, Long size);

    Comment addComment(CommentAddDto comment, long userId, long itemId);

    List<CommentDTO> findItemComments(long itemId);

    List<ItemDto> getItemsWithParam(long userId, Long from, Long size);
}

