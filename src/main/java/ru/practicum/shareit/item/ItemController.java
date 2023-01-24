package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {

    final ItemServiceImpl itemServiceImpl;

    static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemServiceImpl itemServiceImpl) {
        this.itemServiceImpl = itemServiceImpl;
    }

    @PostMapping
    public Item addItem(@Valid @RequestBody Item item, @RequestHeader(HEADER) long userId) {
        return itemServiceImpl.addItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@Valid @PathVariable long itemId, @RequestHeader(HEADER) long userId,
                              @RequestBody Item item) {
        return itemServiceImpl.updateItem(itemId, userId, item);
    }

    @GetMapping("/{itemId}")
    public Item getItemById(@PathVariable long itemId) {
        return itemServiceImpl.getItemById(itemId);
    }

    @GetMapping
    public List<Item> getItems(@RequestHeader(HEADER) long userId) {
        return itemServiceImpl.getItems(userId);
    }

    @GetMapping("/search")
    public List<Item> searchItems(@RequestParam("text") String text) {
        return itemServiceImpl.searchItems(text);
    }
}
