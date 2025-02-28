package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getList();

    UserDto create(UserDto userDto);

    void deleteById(long userId);

    UserDto getUserById(long userId);

    UserDto updateUser(long userId, UserDto userDto);
}
