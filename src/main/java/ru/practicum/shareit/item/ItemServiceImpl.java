package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnavailiableException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ItemServiceImpl {
    ItemRepository itemRepository;

    UserRepository userRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    public List<Item> getItems(long userId) {
        return itemRepository.findByOwnerId(userId);
    }

    public Item addItem(Item item, long userId) {
        if(item.getAvailable() == null) {
            log.error("Не заполнено поле available");
            throw new UnavailiableException("Поле available не может быть пустым");
        }
        if(userRepository.findById(userId).isEmpty()) {
            log.error("Пользователь с Id = {} не найден", userId);
           throw new UserNotFoundException("Пользователь не найден");
        }
        item.setOwnerId(userId);
        return itemRepository.save(item);
    }

    public Item updateItem(long itemId, long userId, Item item) {
        Item itemToUpdate = getItemById(itemId);
        if (itemToUpdate.getOwnerId() != userId) {
            log.error("Неверный Id владельца");
            throw new UserNotFoundException("Владелец не найден");
        }
        if (item.getName() != null)
            itemToUpdate.setName(item.getName());
        if (item.getDescription() != null)
            itemToUpdate.setDescription(item.getDescription());
        if (item.getAvailable() != null)
            itemToUpdate.setAvailable(item.getAvailable());

        log.info("Вещь с Id = {} обновлена", itemId);
        return itemRepository.save(itemToUpdate);
    }

    public Item getItemById(long itemId) {
        if (itemRepository.findById(itemId).isPresent()) {
            return itemRepository.findById(itemId).get();
        }
        throw new ItemNotFoundException("Вещь не найдена");
    }


    public List<Item> searchItems(String text) {
        if(text.isBlank()) {
            return new ArrayList<>();
        }
        String query = text.toLowerCase();
        return itemRepository.search(query);

    }
}
