package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnavailiableException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.InMemoryUserRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Repository
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class InMemoryItemRepository implements ItemRepository {
    @NonFinal
    long generatedId = 0L;

    private long getGeneratedId() {
        return ++generatedId;
    }

    Map<Long, Item> items = new HashMap<>();

    @Override
    public ItemDto addItem(Item item, long userId) {
        if (!InMemoryUserRepository.isPresentUser(userId)) {
            log.error("Пользователь с Id = {} не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (item.getAvailable() == null) {
            log.error("Вещь недоступна");
            throw new UnavailiableException("Вещь не может быть создана");
        }
        item.setId(getGeneratedId());
        item.setOwner(InMemoryUserRepository.getUsers().get(userId));
        items.put(item.getId(), item);
        log.info("Вещь с Id = {} создана", item.getId());
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(long itemId, long userId, Item itemToUpdate) {
        if (isPresentItem(itemId)) {
            Item item = items.get(itemId);
            if (!isOwner(item, userId)) {
                log.error("Неверный Id владельца");
                throw new UserNotFoundException("Владелец не найден");
            }
            if (itemToUpdate.getName() != null)
                item.setName(itemToUpdate.getName());
            if (itemToUpdate.getDescription() != null)
                item.setDescription(itemToUpdate.getDescription());
            if (itemToUpdate.getAvailable() != null)
                item.setAvailable(itemToUpdate.getAvailable());
            items.put(itemId, item);
            log.info("Вещь с Id = {} обновлена", itemId);
            return ItemMapper.toItemDto(item);
        }
        log.error("Вещь с Id = {} не найдена", itemId);
        throw new UserNotFoundException("Вещь не найдена");
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        if (isPresentItem(itemId)) {
            return ItemMapper.toItemDto(items.get(itemId));
        }
        throw new ItemNotFoundException("Вещь не найдена");
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        List<ItemDto> itemsDtos = new ArrayList<>();
        for (Item i : items.values()) {
            if (i.getOwner().getId() == userId) {
                itemsDtos.add(ItemMapper.toItemDto(i));
            }
        }
        return itemsDtos;
    }

    @Override
    public List<ItemDto> searchItems(long userId, String text) {
        List<ItemDto> foundItems = new ArrayList<>();
        if (!text.isBlank()) {
            String query = text.toLowerCase();
            for (Item i : items.values()) {
                if ((i.getName().toLowerCase().contains(query) || i.getDescription().toLowerCase().contains(query))
                        && i.getAvailable()) {
                    foundItems.add(ItemMapper.toItemDto(i));
                }
            }
        }
        return foundItems;
    }

    private boolean isOwner(Item item, long userId) {
        if (item.getOwner().getId() != userId) {
            return false;
        }
        return true;
    }

    private boolean isPresentItem(long itemId) {
        if (items.containsKey(itemId)) {
            return true;
        } else {
            return false;
        }
    }
}
