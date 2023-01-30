package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(long userId);                      //Все бронирования пользователя

    List<Booking> findByBookerIdAndEndIsBeforeOrderByEndDesc(long userId, // Завершенные бронирования пользователя
                                                             LocalDateTime end);

    List<Booking> findByBookerIdAndStartIsAfterOrderByEndDesc(long userId,    //Будущие бронирования пользователя
                                                              LocalDateTime end);

    List<Booking> findByBookerIdAndStatusIsOrderByStartDesc(long userId, BookingStatus status); //Не подтвержденные или
    // отклоненные пользователем бронирования

    List<Booking> findByItemIdOrderByStartDesc(long itemId); //Все бронирования для вещи

    List<Booking> findByItemIdAndEndIsBeforeOrderByEndDesc(long itemId, // Завершенные бронирования для вещи
                                                           LocalDateTime end);

    List<Booking> findByItemIdAndStartIsAfterOrderByEndDesc(long itemId, //Будущие бронирования для вещи
                                                            LocalDateTime end);

    List<Booking> findByItemIdAndStatusIsOrderByStartDesc(long itemId, BookingStatus status); //Не подтвержденные или
    // отклоненные бронирования для вещи

    List<Booking> findByItemIdAndStartBeforeAndEndIsAfterOrderByEndDesc(Long itemId,
                                                                        LocalDateTime start,
                                                                        LocalDateTime end//Текущие бронирования вещи
    );

    List<Booking> findByBookerIdAndStartBeforeAndEndIsAfterOrderByEndDesc(long userId,  //Текущие бронирования пользователя
                                                                          LocalDateTime start, LocalDateTime end);
}
