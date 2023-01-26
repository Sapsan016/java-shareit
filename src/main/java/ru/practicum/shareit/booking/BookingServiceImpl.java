package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.ItemRepository;

import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class BookingServiceImpl {

    BookingRepository bookingRepository;

    UserRepository userRepository;

    ItemRepository itemRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    public Booking addBooking(Booking booking, long userId) {
        System.out.println(booking.toString());
        if (userRepository.findById(userId).isEmpty()) {
            log.error("Пользователь с Id = {} не найден", userId);
            throw new UserNotFoundException("Пользователь не найден");
        }
        if (itemRepository.findById(booking.getItemId()).isEmpty()) {
            log.error("Вещь с Id = {} не найдена", booking.getItemId());
            throw new ItemNotFoundException("Вещь не найдена");
        }
        if (!itemRepository.findById(booking.getItemId()).get().getAvailable()) {
            log.error("Вещь недоступна");
            throw new UnavailiableException("Вещь недоступна");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now()) || booking.getStart().isBefore(LocalDateTime.now()) ||
                booking.getEnd().isBefore(booking.getStart())) {
            log.error("Неверное время начала или конца бониования");
            throw new UnavailiableException("Неверное время начала или конца бронирования");
        }
        booking.setBookerId(userId);
        booking.setStatus(BookingStatus.WAITING);
        return bookingRepository.save(booking);

    }

    public Booking approveBooking(long bookingId, long userId, boolean approved) {
        if (isPresent(bookingId)) {
            Booking bookingToApprove = bookingRepository.findById(bookingId).get();
            if (itemRepository.findById(bookingToApprove.getItemId()).get().getOwnerId() == userId) {
                if (approved) {
                    bookingToApprove.setStatus(BookingStatus.APPROVED);
                    log.info("Бронирование с Id = {} подтверждено", bookingId);
                } else {
                    bookingToApprove.setStatus(BookingStatus.REJECTED);
                    log.info("Бронирование с Id = {} отклонено", bookingId);
                }
                return bookingRepository.save(bookingToApprove);
            }
        }
        log.error("Бронирование с Id = {} не найдено", bookingId);
        throw new BookingNotFoundException("Бронирование не найдено");
    }

    public Booking getBookingById(long bookingId, long userId) {
        if (isPresent(bookingId)) {
            Booking booking = bookingRepository.findById(bookingId).get();
            if (userId == booking.getBookerId() ||
                    userId == itemRepository.findById(booking.getItemId()).get().getOwnerId()) {
                return booking;
            }
        }
        log.error("Бронирование с Id = {} не найдено", bookingId);
        throw new BookingNotFoundException("Бронирование не найдено");
    }

    private boolean isPresent(long bookingId) {
        return bookingRepository.findById(bookingId).isPresent();
    }

    public List<Booking> getUserBooking(String state, long userId) {
        switch (state) {
            case "ALL": {                                                                          //Все бронирования
                return bookingRepository.findByBookerIdOrderByStartDesc(userId);
            }
            case "PAST": {                                                                   // Завершенные бронирования
                return bookingRepository.findByBookerIdAndEndIsBeforeOrderByEndDesc(userId, LocalDateTime.now());
            }
            case "FUTURE": {                                                                  //Будущие бронирования
                return bookingRepository.findByBookerIdAndStartIsBeforeOrderByEndDesc(userId, LocalDateTime.now());
            }
            case "CURRENT": {                                                                  //Текущие бронирования
                return bookingRepository.findByBookerIdAndStartIsAfterOrderByEndDesc(userId, LocalDateTime.now());
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

    public List<Booking> getUserItemBooking(String state, long userId) {
        List<Long> itemList = itemRepository.findItemIdByOwnerId(userId);
        if(itemList.isEmpty()) {
            throw new UnavailiableException("Данный пользователь не имеет вещей");
        }
        List<Booking> bookingList = new ArrayList<>();

        switch (state) {
            case "ALL": {                                                     //Все бронирования для вещей пользователя
                for(Long itemId : itemList) {
                    System.out.println(itemId);
                    bookingList.addAll(bookingRepository.findByItemIdOrderByStartDesc(itemId));
                }
                return bookingList;
            }

            case "PAST": {                                            // Завершенные бронирования для вещей пользователя
                for(Long itemId : itemList) {
                    bookingList.addAll(bookingRepository
                            .findByItemIdAndEndIsBeforeOrderByEndDesc(itemId, LocalDateTime.now()));
                }
                return bookingList;
            }
            case "FUTURE": {                                              //Будущие бронирования для вещей пользователя
                for(Long itemId : itemList) {
                    bookingList.addAll(bookingRepository
                            .findByItemIdAndStartIsBeforeOrderByEndDesc(itemId, LocalDateTime.now()));
                }
                return bookingList;
            }
            case "CURRENT": {                                              //Текущие бронированиядля вещей пользователя
                for(Long itemId : itemList) {
                    bookingList.addAll(bookingRepository
                            .findByItemIdAndStartIsAfterOrderByEndDesc(itemId, LocalDateTime.now()));
                }
                return bookingList;
            }
            case "WAITING": {                                    //Не подтвержденные бонирования для вещей пользователя
                for(Long itemId : itemList) {
                    bookingList.addAll(bookingRepository
                            .findByItemIdAndStatusIsOrderByStartDesc(itemId, BookingStatus.WAITING));
                }
                return bookingList;
            }
            case "REJECTED": {                                        //Отклоненные бронирования для вещей пользователя
                for(Long itemId : itemList) {
                    bookingList.addAll(bookingRepository
                            .findByItemIdAndStatusIsOrderByStartDesc(itemId, BookingStatus.REJECTED));
                }
                return bookingList;
            }

        }
        log.error("Отсутствует метод для параметра = {}", state);
        throw new UnavailiableException("Unknown state: UNSUPPORTED_STATUS");
    }
}
