package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import ru.practicum.shareit.user.validation.Created;
import ru.practicum.shareit.user.validation.Updated;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    @NotBlank(groups = Created.class, message = "Name cannot be blank")
    private String name;
    @Email(groups = {Updated.class, Created.class}, message = "Email is incorrect")
    @NotEmpty(groups = Created.class, message = "Email cannot be empty")
    private String email;
}