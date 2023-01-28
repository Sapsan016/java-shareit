package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(long userId);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByEndDesc(long userId, LocalDateTime end);

    List<Booking> findByBookerIdAndStartIsAfterOrderByEndDesc(long userId, LocalDateTime end);

    List<Booking> findByBookerIdAndStatusIsOrderByStartDesc(long userId, BookingStatus status);

    List<Booking> findByItemIdOrderByStartDesc(long itemId);

    List<Booking> findByItemIdAndEndIsBeforeOrderByEndDesc(long itemId, LocalDateTime end);

    List<Booking> findByItemIdAndStartIsAfterOrderByEndDesc(long itemId, LocalDateTime end);

    List<Booking> findByItemIdAndStatusIsOrderByStartDesc(long itemId, BookingStatus status);

    List<Booking> findByItemIdAndEndIsAfterOrderByEndDesc(Long itemId, LocalDateTime now);

    List<Booking> findByBookerIdAndEndIsAfterOrderByEndDesc(long userId, LocalDateTime now);
}
