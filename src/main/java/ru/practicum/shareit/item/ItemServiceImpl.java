package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item addItem(ItemDto itemDto, long userId) {
        return itemRepository.addItem(itemDto, userId);
    }

    @Override
    public Item updateItem(long itemId, long userId) {
        return null;
    }

    @Override
    public Item getItemById(long itemId, long userId) {
        return null;
    }

    @Override
    public List<Item> getItems(long userId) {
        return null;
    }

    @Override
    public List<Item> searchItems(long userId) {
        return null;
    }
}
