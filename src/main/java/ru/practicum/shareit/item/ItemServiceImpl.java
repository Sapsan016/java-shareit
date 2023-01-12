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
    public ItemDto addItem(Item item, long userId) {
        return itemRepository.addItem(item, userId);
    }

    @Override
    public ItemDto updateItem(long itemId, long userId, Item item) {
        return itemRepository.updateItem(itemId,userId, item);
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        return itemRepository.getItemById(itemId,userId);
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        return itemRepository.getItems(userId);
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        return itemRepository.searchItems(userId, text);
    }
}
