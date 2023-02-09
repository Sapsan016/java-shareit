package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;

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
    public ItemDto addItem(@Valid @RequestBody ItemAddDto itemAddDto, @RequestHeader(HEADER) long userId) { //добавить вещь
        return ItemMapper.toItemDto(itemService.addItem(itemAddDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Valid @PathVariable long itemId, @RequestHeader(HEADER) long userId, //Обновить вещь
                              @RequestBody ItemAddDto itemAddDto) {
        return ItemMapper.toItemDto(itemService.updateItem(itemId, userId, itemAddDto));
    }

    @GetMapping("/{itemId}")                                                                       //Получить вещь
    public ItemDto getItemById(@PathVariable long itemId, @RequestHeader(HEADER) long userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping                                                                                 //Получить все вещи
    public List<ItemDto> getItems(@RequestHeader(HEADER) long userId,
                                  @RequestParam(required = false) Long from,
                                  @RequestParam(required = false) Long size) {
        if (from == null || size == null) {
            return itemService.getItems(userId);
        }
        return itemService.getItemsWithParam(userId, from, size);
    }

    @GetMapping("/search")                                                                          //Найти вещи
    public List<ItemDto> searchItems(@RequestParam("text") String text,
                                     @RequestParam(required = false) Long from,
                                     @RequestParam(required = false) Long size) {
        if (from == null || size == null) {
            return itemService.searchItems(text).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
        return itemService.searchItemsWithParams(text, from, size).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/{itemId}/comment")                                                        //Добавить отзыв о вещи
    public CommentDTO addComment(@Valid @RequestBody CommentAddDto commentAddDto, @RequestHeader(HEADER) long userId,
                                 @PathVariable long itemId) {
        return CommentMapper.toCommentDto(itemService.addComment(commentAddDto, userId, itemId));
    }
}