package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class User {
    private Long id;
    private String name;
    private String email;

    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}
