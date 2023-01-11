package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.Map;


@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InMemoryItemRepository implements ItemRepository {

    Map<Long, ItemDto> items = new HashMap<>();

    @Override
    public Item addItem(ItemDto itemDto, long userId) {
        return null;
    }
}
