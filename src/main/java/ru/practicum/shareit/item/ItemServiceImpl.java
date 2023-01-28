package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnavailiableException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ItemServiceImpl implements ItemService {
    ItemRepository itemRepository;

    UserRepository userRepository;

    CommentRepository commentRepository;

    BookingRepository bookingRepository;

    public ItemServiceImpl(ItemRepository itemRepository, UserRepository userRepository, CommentRepository commentRepository, BookingRepository bookingRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public List<Item> getItems(long userId) {
        List<Item> itemList = itemRepository.findByOwnerIdOrderById(userId);
        boolean isOwner = true;
        List<Booking> bookingsList = new ArrayList<>();
        for (Item item : itemList) {
            if(item.getOwnerId() != userId) {
                isOwner = false;
            }
            bookingsList.addAll(bookingRepository.findByItemIdOrderByStartDesc(item.getId()));
        }
        if(!isOwner) {
            return itemList;
        }
        for (Booking b : bookingsList) {
            for (Item item : itemList) {
                if (b.getItemId() == item.getId()) {
                    item.setNextBookingId(bookingsList.get(0).getId());
                    item.setLastBookingId(bookingsList.get(1).getId());
                }
            }
        }
        return itemList;
    }

    @Override
    public Item addItem(Item item, long userId) {
        if (item.getAvailable() == null) {
            log.error("Не заполнено поле available");
            throw new UnavailiableException("Поле available не может быть пустым");
        }
        if (userRepository.findById(userId).isEmpty()) {
            log.error("Пользователь с Id = {} не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        item.setOwnerId(userId);
        log.info("Новая вещь добавлена");
        return itemRepository.save(item);
    }

    @Override
    public Item updateItem(long itemId, long userId, Item item) {
        Item itemToUpdate = getItemById(itemId, userId);
        if (itemToUpdate.getOwnerId() != userId) {
            log.error("Неверный Id владельца");
            throw new UserNotFoundException("Владелец не найден");
        }
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
    public Item getItemById(long itemId, long userId) {
        if (itemRepository.findById(itemId).isPresent()) {
            Item item = itemRepository.findById(itemId).get();
            if(item.getOwnerId() != userId) {
                return item;
            }
            List<Booking> bookings = bookingRepository.findByItemIdOrderByStartDesc(itemId);
            if(bookings.isEmpty()) {
                return item;
            }
            item.setNextBookingId(bookings.get(0).getId());
            item.setLastBookingId(bookings.get(1).getId());
            return item;
        }
        log.error("Вещь с Id = {} не найдена", itemId);
        throw new ItemNotFoundException("Вещь не найдена");
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
    public Comment addComment(Comment comment, long userId, long itemId) {
        if (userId == bookingRepository.findById(itemId).get().getBookerId() &&
                bookingRepository.findById(itemId).get().getEnd().isBefore(LocalDateTime.now())) {
            log.info("Добавлен новый отзыв к вещи с Id = {}", itemId);
            return commentRepository.save(comment);
        }
        log.error("Невеный Id пользователя или срок аенды не завершен");
        throw new UserNotFoundException("Невеный Id пользователя");
    }
}