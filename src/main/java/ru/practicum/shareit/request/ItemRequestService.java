package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestService {
    ItemRequest addRequest(ItemRequestAddDto itemRequestAddDto, long userId);

    ItemRequestDto getRequestById(long requestId, long userId);
}
