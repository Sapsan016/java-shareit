package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody Item item, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.addItem(item,userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Valid @PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId,
    @RequestBody Item item) {
        return itemService.updateItem(itemId,userId, item);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemById(itemId,userId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestParam("text") String text) {
        return itemService.searchItems(userId, text);
    }
}
