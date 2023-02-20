package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class BookItemRequestDto {
    long itemId;
    @FutureOrPresent(message = "Время начала бронирования не может быть в прошлом")
    LocalDateTime start;
    @Future(message = "Время окончания бронирования должно быть в будущем")
    LocalDateTime end;
}