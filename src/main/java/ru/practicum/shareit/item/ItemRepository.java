package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

@Component
public interface ItemRepository {


    ItemDto addItem(Item item, long userId);

    ItemDto updateItem(long itemId, long userId, Item item);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getItems(long userId);

    List<ItemDto> searchItems(long userId, String text);
}
