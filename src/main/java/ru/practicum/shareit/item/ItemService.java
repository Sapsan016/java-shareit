package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentAddDto;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemAddDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItems(long userId);

    Item addItem(ItemAddDto itemAddDto, long userId);


    Item updateItem(long itemId, long userId, ItemAddDto item);

    ItemDto getItemById(long itemId, long userId);

    List<Item> searchItems(String text);

    Comment addComment(CommentAddDto comment, long userId, long itemId);

    List<CommentDTO> findItemComments(long itemId);
}

