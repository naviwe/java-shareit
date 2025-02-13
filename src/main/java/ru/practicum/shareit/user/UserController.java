package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        log.info("Creating new user: {}", userDto);
        var createdUser = userService.create(userDto);
        log.info("User created successfully: {}", createdUser);
        return createdUser;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto,
                          @PathVariable Long userId) {
        log.info("Updating user with ID {}: {}", userId, userDto);
        var updatedUser = userService.update(userId, userDto);
        log.info("User updated successfully: {}", updatedUser);
        return updatedUser;
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        log.info("Fetching user with ID: {}", userId);
        var user = userService.get(userId);
        log.info("User found: {}", user);
        return user;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Deleting user with ID: {}", userId);
        userService.delete(userId);
        log.info("User with ID {} deleted successfully", userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Fetching all users");
        var users = userService.getAll();
        log.info("Total users found: {}", users.size());
        return users;
    }
}

