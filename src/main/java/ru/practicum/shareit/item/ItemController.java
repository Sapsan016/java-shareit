package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/items")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemController {

    final ItemService itemService;

    static final String HEADER = "X-Sharer-User-Id";


    public ItemController(ItemServiceImpl itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@Valid @RequestBody Item item, @RequestHeader(HEADER) long userId) {
        return ItemMapper.toItemDto(itemService.addItem(item, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Valid @PathVariable long itemId, @RequestHeader(HEADER) long userId,
                              @RequestBody Item item) {
        return ItemMapper.toItemDto(itemService.updateItem(itemId, userId, item));
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable long itemId) {
        return ItemMapper.toItemDto(itemService.getItemById(itemId));
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(HEADER) long userId) {
        return itemService.getItems(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String text) {
        return itemService.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
    @PostMapping("/{itemId}/comment")
    public CommentDTO addComment(@Valid @RequestBody Comment comment, @RequestHeader(HEADER) long userId,
                                 @PathVariable String itemId) {
        return ItemMapper.toItemDto(itemService.addItem(comment, userId, itemId));
    }
}