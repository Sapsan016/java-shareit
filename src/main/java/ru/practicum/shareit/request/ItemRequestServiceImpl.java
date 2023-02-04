package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestAddDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    ItemRequestRepository requestRepository;
    UserRepository userRepository;

    public ItemRequestServiceImpl(ItemRequestRepository requestRepository, UserRepository userRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
    }


    @Override
    public ItemRequest addRequest(ItemRequestAddDto itemRequestAddDto, long userId) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestAddDto);
        System.out.println(itemRequest);
        User requester = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("Пользоватеь не найден"));
        itemRequest.setRequester(requester);
        log.info("Новый запрос добавлен");
        return requestRepository.save(itemRequest);
    }

}
