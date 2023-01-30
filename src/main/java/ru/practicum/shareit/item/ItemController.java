package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
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
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader(HEADER) long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader(HEADER) long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String text) {
        return itemService.searchItems(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")
    public CommentDTO addComment(@Valid @RequestBody CommentAddDto commentAddDto, @RequestHeader(HEADER) long userId,
                                 @PathVariable long itemId) {
        System.out.println(commentAddDto.toString());
        return CommentMapper.toCommentDto(itemService.addComment(commentAddDto, userId, itemId));
    }
}