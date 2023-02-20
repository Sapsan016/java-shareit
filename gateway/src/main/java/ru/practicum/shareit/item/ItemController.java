package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestBody @Valid ItemRequestDto requestDto) {
        log.info("Добавляется вещь {}, пользователем userId={}", requestDto.toString(), userId);
        return itemClient.addItem(userId, requestDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @RequestBody ItemRequestDto requestDto) {
        log.info("Обновляется вещь {}, пользователем userId={}", requestDto.toString(), userId);
        return itemClient.updateItem(userId, itemId, requestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long itemId) {
        log.info("Запрашивается вещь {}, пользователем userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Запрашивается список всех вещей пользователем userId={}", userId);
        return itemClient.getItems(userId);
    }
}
