package ru.practicum.shareit.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString
public class UserRequestDto {
    @NotBlank(message = "Поле name не должно быть пустым")
    String name;
    @NotBlank(message = "Поле email не должно быть пустым")
    @Email(message = "Поле email должно соотвествовать формату")
    String email;

}
