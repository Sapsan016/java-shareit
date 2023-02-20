package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;

    UserRepository userRepository;

    CommentRepository commentRepository;

    BookingRepository bookingRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository,
                           CommentRepository commentRepository,
                           BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<ItemDto> getItems(long userId) {
        List<Item> itemList = itemRepository.findByOwnerIdOrderById(userId);
        boolean isOwner = true;
        List<Booking> bookingsList = new ArrayList<>();
        for (Item item : itemList) {
            if (item.getOwnerId() != userId) {
                isOwner = false;
            }
            bookingsList.addAll(bookingRepository.findByItemIdOrderByStartDesc(item.getId()));
        }
        if (!isOwner) {
            throw new UserNotFoundException("Пользователь не является хозяином вещей");
        }
        List<ItemDto> dtoList = itemList.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        for (Booking b : bookingsList) {
            for (ItemDto dto : dtoList) {
                if (b.getItem().getId() == dto.getId()) {
                    dto.setNextBooking(BookingMapper.toBookingDto(bookingsList.get(0)));
                    dto.setLastBooking(BookingMapper.toBookingDto(bookingsList.get(1)));
                }
            }
        }
        return dtoList;
    }

    @Override
    public Item addItem(ItemAddDto itemAddDto, long userId) { //Добавить вещь
        Item item = ItemMapper.toItem(itemAddDto);
        if (userRepository.findById(userId).isEmpty()) {
            log.error("Пользователь с Id = {} не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        item.setOwnerId(userId);
        log.info("Новая вещь добавлена");
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(long itemId, long userId, ItemAddDto itemAddDto) {
        Item itemToUpdate = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещь с id %s не найдена", itemId)));
        if (itemToUpdate.getOwnerId() != userId) {
            log.error("Неверный Id владельца");
            throw new UserNotFoundException("Владелец не найден");
        }
        Item item = ItemMapper.toItem(itemAddDto);
        if (item.getName() != null)
            itemToUpdate.setName(item.getName());
        if (item.getDescription() != null)
            itemToUpdate.setDescription(item.getDescription());
        if (item.getAvailable() != null)
            itemToUpdate.setAvailable(item.getAvailable());
        log.info("Вещь с Id = {} обновлена", itemId);
        return itemRepository.save(itemToUpdate);
    }

    @Override
    public ItemDto getItemById(long itemId, long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещь с id %s не найдена", itemId)));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setComments(findItemComments(itemId));
        if (item.getOwnerId() != userId) {
            return itemDto;
        }
        List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(itemId);
        if (bookings.isEmpty()) {
            return itemDto;
        }
        if (bookings.size() > 1) {
            itemDto.setNextBooking(BookingMapper.toBookingDto(bookings.get(0)));
            itemDto.setLastBooking(BookingMapper.toBookingDto(bookings.get(1)));
        }
        if (bookings.size() == 1) {
            itemDto.setNextBooking(BookingMapper.toBookingDto(bookings.get(0)));
            itemDto.setLastBooking(BookingMapper.toBookingDto(bookings.get(0)));
        }
        return itemDto;
    }

    @Override
    public List<Item> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        String query = text.toLowerCase();
        return itemRepository.search(query);
    }

    @Override
    public List<Item> searchItemsWithParams(String text, Long from, Long size) {
        return searchItems(text).stream()
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    @Override
    public Comment addComment(CommentAddDto commentAddDto, long userId, long itemId) {
        Comment comment = CommentMapper.toComment(commentAddDto);
        if (validateCommentAuthorAndDate(userId, itemId)) {
            comment.setItemId(itemId);
            comment.setAuthor(userRepository.findById(userId).orElseThrow(()
                    -> new UserNotFoundException("Владелец не найден")));
            comment.setCreated(LocalDateTime.now());
            log.info("Добавлен новый отзыв к вещи с Id = {}", itemId);
            return commentRepository.save(comment);
        }
        log.error("Неверные данные бронирования");
        throw new UnavailableException("Невозможно добавить отзыв. Проверьте данные бронирования");
    }

    @Override
    public List<CommentDTO> findItemComments(long itemId) {
        List<Comment> comments = commentRepository.findByItemId(itemId);
        return comments.stream().map(CommentMapper::toCommentDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsWithParam(long userId, Long from, Long size) {
        return getItems(userId).stream()
                .skip(from)
                .limit(size)
                .collect(Collectors.toList());
    }

    private boolean validateCommentAuthorAndDate(long userId, long itemId) {
        List<Booking> bookings = bookingRepository.findByItemIdAndEndIsBeforeOrderByEndDesc(itemId, LocalDateTime.now());
        if (bookings.isEmpty()) {
            return false;
        }
        for (Booking booking : bookings) {
            return userId == booking.getBooker().getId() && (booking.getStatus().equals(BookingStatus.APPROVED));
        }
        return true;
    }
}