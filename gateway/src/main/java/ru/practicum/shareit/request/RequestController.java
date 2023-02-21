package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {

    private final RequestClient requestClient;
    static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(HEADER) long userId,
                                             @RequestBody @Valid RequestDto requestDto) {
        log.info("Добавляется запрос {}, пользователем userId={}", requestDto.toString(), userId);
        return requestClient.addRequest(userId, requestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(HEADER) long userId,
                                             @PathVariable long requestId) {
        log.info("Запрашивается запрос {}, пользователем userId={}", requestId, userId);
        return requestClient.getRequest(requestId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllRequests(@RequestHeader(HEADER) long userId) {
        log.info("Запрашивается список всех запросовзов");
        return requestClient.getAllRequests(userId);
    }


    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsForUser(@RequestHeader(HEADER) long userId,
                                                        @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                        @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Запрашивается список всех запросов пользователем userId={}", userId);
        return requestClient.getRequests(userId, from, size);
    }
}
