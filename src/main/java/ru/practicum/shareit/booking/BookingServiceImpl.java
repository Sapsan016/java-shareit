package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingAddDto;
import ru.practicum.shareit.booking.model.Booking;
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
    public Booking addBooking(BookingAddDto bookingAddDto, long userId) {               //Добавить бронирование
        User booker = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("Пользователь с id %s не найден",userId)));
        Item item = itemRepository.findById(bookingAddDto.getItemId()).orElseThrow(() ->
                new ItemNotFoundException(String.format("Вещь с id %s не найдена", bookingAddDto.getItemId())));
        Booking booking = BookingMapper.toBooking(bookingAddDto);
        validateBooking(booker.getId(), booking, item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        Booking bookingSaved = bookingRepository.save(booking);
        log.info("Бронирование id = {} сохранено", bookingSaved.getId());
        return bookingSaved;
    }

    private void validateBooking(long bookerId, Booking booking, Item item) {                  //Проверить бронирование
        if (bookerId == item.getOwnerId()) {
            throw new UserNotFoundException("Владелец не может бронировать свои вещи.");
        } else if (!item.getAvailable()) {
            throw new UnavailiableException(String.format("Вещь с id %d не доступна для бронирования.",
                    item.getId()));
        } else if (validateDate(booking.getStart(), booking.getEnd())) {
            throw new InvalidDataException("Неправильное время начала или конца бронирования.");
        }
    }

    private boolean validateDate(LocalDateTime startBooking, LocalDateTime endBooking) {       //Проверить дату и время
        return startBooking.isBefore(LocalDateTime.now()) || endBooking.isBefore(LocalDateTime.now())
                || endBooking.isBefore(startBooking);
    }

    @Override
    public Booking approveBooking(long bookingId, long userId, boolean approved) { //Подтвердить/отклонить бронирование
        Booking bookingToApprove = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException(String.format("Бронирование с id %s не найдено", bookingId)));
        if (bookingToApprove.getStatus().equals(BookingStatus.APPROVED)) {
            log.error("Бронирование с id = {} уже подтверждено", bookingId);
            throw new UnavailiableException("Бронирование уже подтверждено");
        }
        if (itemRepository.findById(bookingToApprove.getItem().getId()).orElseThrow().getOwnerId() != userId) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не является владельцем вещи.",
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
    public Booking getBookingById(long bookingId, long userId) {                           // Получить бронирование
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException(String.format("Бронирование с id %d не найдено.", bookingId)));
        if (userId == booking.getBooker().getId() ||
                userId == itemRepository.findById(booking.getItem().getId()).orElseThrow().getOwnerId()) {
            return booking;
        }
        throw new UserNotFoundException("Неверное id владельца");
    }


    @Override
    public List<Booking> getUserBooking(String state, long userId) {       //Получить все бронирования для пользователя
        if (isUserPresent(userId)) {
            log.error("Пользователь с Id = {} не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        switch (state) {
            case "ALL": {                                                                          //Все бронирования
                return bookingRepository.findByBookerIdOrderByStartDesc(userId);
            }
            case "PAST": {                                                                   // Завершенные бронирования
                return bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(userId,
                        LocalDateTime.now());
            }
            case "FUTURE": {                                                                  //Будущие бронирования
                return bookingRepository.findByBookerIdAndStartIsAfterOrderByEndDesc(userId,
                        LocalDateTime.now());
            }
            case "CURRENT": {                                                                  //Текущие бронирования
                return bookingRepository.findByBookerIdAndStartBeforeAndEndIsAfterOrderByEndDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now());
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
    public List<Booking> getUserItemBooking(String state, long userId) {           //Получить все бронирования для вещи
        if (isUserPresent(userId)) {
            log.error("Пользователь с Id = {} не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        List<Long> itemList = itemRepository.findItemIdByOwnerId(userId);               //Список Id вещей пользователя
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
                            .findByItemIdAndEndIsBeforeOrderByEndDesc(itemId,
                                    LocalDateTime.now()));
                }
                return bookingList;
            }
            case "FUTURE": {                                              //Будущие бронирования для вещей пользователя
                for (Long itemId : itemList) {
                    bookingList.addAll(bookingRepository
                            .findByItemIdAndStartIsAfterOrderByEndDesc(itemId,
                                    LocalDateTime.now())
                    );
                }
                return bookingList;
            }
            case "CURRENT": {                                              //Текущие бронированиядля вещей пользователя
                for (Long itemId : itemList) {
                    bookingList.addAll(bookingRepository
                            .findByItemIdAndStartBeforeAndEndIsAfterOrderByEndDesc(itemId,
                                    LocalDateTime.now(), LocalDateTime.now()));
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

