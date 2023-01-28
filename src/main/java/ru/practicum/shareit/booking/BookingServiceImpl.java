package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BookingServiceImpl implements BookingService {

    BookingRepository bookingRepository;

    UserRepository userRepository;

    ItemRepository itemRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public Booking addBooking(Booking booking, long userId) {
        User booker = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользоватеь не найден"));
        Item item = itemRepository.findById(booking.getItemId()).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещь с id %s не найдена", booking.getItemId())));
        validateBooking(booker.getId(), booking, item);
        booking.setBookerId(booker.getId());
        booking.setStatus(BookingStatus.WAITING);
        booking.setItemId(item.getId());
        Booking bookingSaved = bookingRepository.save(booking);
        log.info("Бронирование id = {} сохранено", bookingSaved.getId());
        return bookingSaved;
    }

    private void validateBooking(long bookerId, Booking booking, Item item) {
        if (bookerId == item.getOwnerId()) {
            throw new UnavailiableException("Владелец не может бронировать свои вещи.");
        } else if (!item.getAvailable()) {
            throw new UnavailiableException(String.format("Вещь с id %d не доступна для бронирования.",
                    item.getId()));
        } else if (validateDate(booking.getStart(), booking.getEnd())) {
            throw new InvalidDataException("Неравлиьное время начала или конца бронирования.");
        }
    }

    private boolean validateDate(LocalDateTime startBooking, LocalDateTime endBooking) {
        return startBooking.isBefore(LocalDateTime.now()) || endBooking.isBefore(LocalDateTime.now())
                || endBooking.isBefore(startBooking);
    }

    @Override
    public Booking approveBooking(long bookingId, long userId, boolean approved) {

        Booking bookingToApprove = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException(String.format("Бронирование с id %s не найдено", bookingId)));
        if (bookingToApprove.getStatus().equals(BookingStatus.APPROVED)) {
            log.error("Бронирование с id = {} уже подтверждено", bookingId);
            throw new UnavailiableException("Бронирование уже подтверждено");
        }
        if (itemRepository.findById(bookingToApprove.getItemId()).orElseThrow().getOwnerId() != userId) {
            throw new InvalidUserException(String.format("Пользователь с id %d не является владельцем вещи.",
                    userId));
        }
        if (approved) {
            bookingToApprove.setStatus(BookingStatus.APPROVED);
            log.info("Бронирование с Id = {} подтверждено", bookingId);
        } else {
            bookingToApprove.setStatus(BookingStatus.REJECTED);
            log.info("Бронирование с Id = {} отклонено", bookingId);
        }
        return bookingRepository.save(bookingToApprove);
    }

    @Override
    public Booking getBookingById(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException(String.format("Бронирование с id %d не найдено.", bookingId)));
        if (userId == booking.getBookerId() ||
                userId == itemRepository.findById(booking.getItemId()).orElseThrow().getOwnerId()) {
            return booking;
        }
        throw new UserNotFoundException("Неверное id владельца");
    }


    @Override
    public List<Booking> getUserBooking(String state, long userId) {
        if (isUserPresent(userId)) {
            log.error("Пользователь с Id = {} не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        switch (state) {
            case "ALL": {                                                                          //Все бронирования
                return bookingRepository.findByBookerIdOrderByStartDesc(userId);
            }
            case "PAST": {                                                                   // Завершенные бронирования
                return bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(userId, LocalDateTime.now());
            }
            case "FUTURE": {                                                                  //Будущие бронирования
                return bookingRepository.findByBookerIdAndStartIsAfterOrderByEndDesc(userId, LocalDateTime.now());
            }
            case "CURRENT": {                                                                  //Текущие бронирования
                return bookingRepository.findByBookerIdAndEndIsAfterOrderByEndDesc(userId, LocalDateTime.now());
            }
            case "WAITING": {                                                          //Не подтвержденные бонирования
                return bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.WAITING);
            }
            case "REJECTED": {                                                             //Отклоненные бронирования
                return bookingRepository.findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED);
            }

        }
        log.error("Отсутствует метод для параметра = {}", state);
        throw new UnavailiableException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<Booking> getUserItemBooking(String state, long userId) {
        if (isUserPresent(userId)) {
            log.error("Пользователь с Id = {} не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        List<Long> itemList = itemRepository.findItemIdByOwnerId(userId);  //Список Id вещей пользователя
        if (itemList.isEmpty()) {
            throw new UnavailiableException("Данный пользователь не имеет вещей");
        }
        List<Booking> bookingList = new ArrayList<>();
        switch (state) {
            case "ALL": {                                                     //Все бронирования для вещей пользователя
                for (Long itemId : itemList) {
                    bookingList.addAll(bookingRepository.findByItemIdOrderByStartDesc(itemId));
                }
                return bookingList;
            }

            case "PAST": {                                            // Завершенные бронирования для вещей пользователя
                for (Long itemId : itemList) {
                    bookingList.addAll(bookingRepository
                            .findByItemIdAndEndIsBeforeOrderByEndDesc(itemId, LocalDateTime.now()));
                }
                return bookingList;
            }
            case "FUTURE": {                                              //Будущие бронирования для вещей пользователя
                for (Long itemId : itemList) {
                    bookingList.addAll(bookingRepository
                            .findByItemIdAndStartIsAfterOrderByEndDesc(itemId, LocalDateTime.now()));
                }
                return bookingList;
            }
            case "CURRENT": {                                              //Текущие бронированиядля вещей пользователя
                for (Long itemId : itemList) {
                    bookingList.addAll(bookingRepository
                            .findByItemIdAndEndIsAfterOrderByEndDesc(itemId, LocalDateTime.now()));
                }
                return bookingList;
            }
            case "WAITING": {                                    //Не подтвержденные бонирования для вещей пользователя
                for (Long itemId : itemList) {
                    bookingList.addAll(bookingRepository
                            .findByItemIdAndStatusIsOrderByStartDesc(itemId, BookingStatus.WAITING));
                }
                return bookingList;
            }
            case "REJECTED": {                                        //Отклоненные бронирования для вещей пользователя
                for (Long itemId : itemList) {
                    bookingList.addAll(bookingRepository
                            .findByItemIdAndStatusIsOrderByStartDesc(itemId, BookingStatus.REJECTED));
                }
                return bookingList;
            }

        }
        log.error("Отсутствует метод для параметра = {}", state);
        throw new UnavailiableException("Unknown state: UNSUPPORTED_STATUS");
    }

    private boolean isUserPresent(long userId) {
        return userRepository.findById(userId).isEmpty();
    }
}

