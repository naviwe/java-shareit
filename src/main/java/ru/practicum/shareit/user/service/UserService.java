package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto user);

    UserDto update(Long id, UserDto user);

    UserDto get(Long id);

    void delete(Long id);

    List<UserDto> getAll();
}
