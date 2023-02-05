package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidDataException;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    ItemRequestRepository requestRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository requestRepository,
                                  UserRepository userRepository,
                                  ItemRepository itemRepository) {
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
        ItemRequest request = requestRepository.findById(requestId).orElseThrow(() ->
                new ItemRequestNotFoundException(String.format("Запрос с id %s не найден", requestId)));
        List<Item> items = findItems(requestId);
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(request);
        itemRequestDto.setItems(items);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getRequestsForUser(long userId) {
        findRequester(userId);
        log.info("Выполняется поиск всех запросов для пользователя с Id = {}", userId);
        List<ItemRequest> requestList = requestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        return requestList.stream()
                .map(ItemRequestMapper::toItemRequestDto)
                .peek((requestDto) -> requestDto.setItems(findItems(requestDto.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequestsWithParam(long userId, long from, long size) {
        if (from < 0 || size <= 0) {
            log.error("Неверные параметры from {} или size {}", from, size);
            throw new InvalidDataException("Неверные параметры");
        }
        findRequester(userId);
        log.info("Выполняется поиск всех запросов с начиная с номера {} количество запросов {}", from, size);
        List<ItemRequest> requestList = findAll();
        if(isRequester(requestList, userId)) {
            return new ArrayList<>();
        }
        return requestList.stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .map(ItemRequestMapper::toItemRequestDto)
                .peek((requestDto) -> requestDto.setItems(findItems(requestDto.getId())))
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId) {
        findRequester(userId);
        List<ItemRequest> requestList = findAll();
        if(isRequester(requestList, userId)) {
                return new ArrayList<>();
            }
        return requestList.stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .map(ItemRequestMapper::toItemRequestDto)
                .peek((requestDto) -> requestDto.setItems(findItems(requestDto.getId())))
                .collect(Collectors.toList());
    }

    private User findRequester(long userId) {
        log.info("Выполняется поиск пользоватля с Id = {}", userId);
        return userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользователь не найден"));
    }

    private List<Item> findItems(long requestId) {
        log.info("Выполняется поиск вещей добавленных по запросу с id = {}", requestId);
        return itemRepository.findByRequestId(requestId);
    }

    private List<ItemRequest> findAll() {
        log.info("Выполняется поиск всех запросов");
        return requestRepository.findAll();
    }

    private boolean isRequester(List<ItemRequest> requestList, long userId) {
        for (ItemRequest request : requestList) {
            if (request.getRequester().getId() == userId) {
                return true;
            }
        }
        return false;
    }
}
