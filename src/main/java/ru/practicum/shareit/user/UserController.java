package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

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
        log.info("Received request to create user: {}", userDto);
        var user = userService.create(UserMapper.toUser(userDto));
        log.info("User created successfully: {}", user);
        return UserMapper.toUserDto(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto,
                          @PathVariable Long userId) {
        log.info("Received request to update user ID: {} with data: {}", userId, userDto);
        var user = UserMapper.toUser(userDto);
        user.setId(userId);
        var updatedUser = userService.update(user);
        log.info("User updated successfully: {}", updatedUser);
        return UserMapper.toUserDto(updatedUser);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        log.info("Fetching user with ID: {}", userId);
        var user = userService.get(userId);
        log.info("User found: {}", user);
        return UserMapper.toUserDto(user);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Received request to delete user with ID: {}", userId);
        userService.delete(userId);
        log.info("User with ID {} deleted successfully", userId);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.info("Fetching all users");
        var users = userService.getAll();
        log.info("Total users found: {}", users.size());
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }
}
