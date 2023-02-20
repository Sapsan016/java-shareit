package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;
    static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(HEADER) long userId,
                                          @RequestBody @Valid ItemRequestDto requestDto) {
        log.info("Добавляется вещь {}, пользователем userId={}", requestDto.toString(), userId);
        return itemClient.addItem(userId, requestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HEADER) long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemRequestDto requestDto) {
        log.info("Обновляется вещь {}, пользователем userId={}", requestDto.toString(), userId);
        return itemClient.updateItem(userId, itemId, requestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(HEADER) long userId,
                                          @PathVariable long itemId) {
        log.info("Запрашивается вещь {}, пользователем userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader(HEADER) long userId,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Запрашивается список всех вещей пользователем userId={}", userId);
        return itemClient.getItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(HEADER) long userId,
                                              @RequestParam("text") String text,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Поиск вещей по строке {} пользователем userId={}", text, userId);
        return itemClient.searchItems(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HEADER) long userId,
                                             @PathVariable long itemId,
                                             @RequestBody @Valid CommentRequestDto requestDto) {
        log.info("Добавляется комментарий {} пользователем userId={}", requestDto.toString(), userId);
        return itemClient.addComment(userId, itemId, requestDto);
    }
}
