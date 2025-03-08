package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> get() {
        log.info("Получение списка пользователей");
        List<UserDto> result = userService.getList();
        log.info("Список пользователей получен, количество: {}", result.size());
        return result;
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId) {
        log.info("Получение пользователя с ID: {}", userId);
        UserDto result = userService.getUserById(userId);
        log.info("Пользователь с ID: {} получен: {}", userId, result);
        return result;
    }

    @PatchMapping("/{userId}")
    public UserDto patch(@PathVariable long userId,
                         @RequestBody UserDto userDto) {
        log.info("Обновление пользователя с ID: {}, новые данные: {}", userId, userDto);
        UserDto result = userService.updateUser(userId, userDto);
        log.info("Пользователь с ID: {} успешно обновлен: {}", userId, result);
        return result;
    }

    @PostMapping
    public UserDto post(@Valid @RequestBody UserDto userDto) {
        log.info("Создание нового пользователя: {}", userDto);
        UserDto result = userService.create(userDto);
        log.info("Пользователь успешно создан: {}", result);
        return result;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Удаление пользователя с ID: {}", userId);
        userService.deleteById(userId);
        log.info("Пользователь с ID: {} успешно удален", userId);
    }
}
