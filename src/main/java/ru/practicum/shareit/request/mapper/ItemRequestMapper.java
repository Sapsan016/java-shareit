package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;


public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestAddDto itemRequestAddDto) {
        return new ItemRequest(
                0,
                itemRequestAddDto.getDescription(),
                null,
                LocalDateTime.now()
        );
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequester(),
                itemRequest.getCreated()
        );
    }
}
