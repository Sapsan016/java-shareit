package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    ItemRequestRepository requestRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository requestRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }


    @Override
    public ItemRequest addRequest(ItemRequestAddDto itemRequestAddDto, long userId) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestAddDto);
        itemRequest.setRequester(findRequester(userId));
        log.info("Новый запрос добавлен");
        return requestRepository.save(itemRequest);
    }

    @Override
    public ItemRequestDto getRequestById(long requestId, long userId) {
        findRequester(userId);
        log.info("Выполняется поиск запроса с Id = {}", requestId);
        ItemRequest request =  requestRepository.findById(requestId).orElseThrow(() ->
                new ItemRequestNotFoundException(String.format("Запрос с id %s не найден", requestId)));
        List<Item> items = itemRepository.findByRequestId(requestId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(request);
        itemRequestDto.setItems(items);
        return itemRequestDto;

    }
    private User findRequester(long userId) {
        log.info("Выполняется поиск пользоватля с Id = {}", userId);
        return userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользоватеь не найден"));
    }

}
