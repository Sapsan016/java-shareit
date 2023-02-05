package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestController {

    final ItemRequestService requestService;

    static final String HEADER = "X-Sharer-User-Id";

    public ItemRequestController(ItemRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto addRequest(@Valid @RequestBody ItemRequestAddDto itemRequestAddDto,         //добавить запрос
                                     @RequestHeader(HEADER) long userId) {
        return ItemRequestMapper.toItemRequestDto(requestService.addRequest(itemRequestAddDto, userId));
    }
    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable long requestId,                               // получить запрос
                                     @RequestHeader(HEADER) long userId) {
        return requestService.getRequestById(requestId, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getRequests(@RequestHeader(HEADER) long userId) {      //получить список своих запросов
        return requestService.getRequestsForUser(userId);
    }

}
