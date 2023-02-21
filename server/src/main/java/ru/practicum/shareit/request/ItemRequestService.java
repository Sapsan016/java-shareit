package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {
    ItemRequest addRequest(ItemRequestAddDto itemRequestAddDto, long userId);

    ItemRequestDto getRequestById(long requestId, long userId);

    List<ItemRequestDto> getRequestsForUser(long userId);

    List<ItemRequestDto> getAllRequestsWithParam(long userId, long from, long size);

}
