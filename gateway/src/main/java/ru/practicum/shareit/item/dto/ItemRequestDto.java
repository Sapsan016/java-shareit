package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    @NotBlank(message = "Поле name не должно быть пустым")
    String name;
    @NotBlank(message = "Поле description не должно быть пустым")
    String description;
    @NotNull(message = "Поле available не должно быть пустым")
    Boolean available;
    Long requestId;
}
