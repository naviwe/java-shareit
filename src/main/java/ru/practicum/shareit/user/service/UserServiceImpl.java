package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.EmailException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        validate(user);
        checkEmailDuplicate(user.getId(), user.getEmail());
        return UserMapper.toUserDto(userStorage.create(user));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        String email = userDto.getEmail();
        if (email != null && !email.isBlank()) {
            checkEmailDuplicate(userId, email);
        }
        User user = userStorage.get(userId)
                .orElseThrow(() -> new NotFoundException("User with ID #" + userId + " does not exist."));
        String name = userDto.getName();
        if (name != null && !name.isBlank()) {
            user.setName(name);
        }
        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }

        return UserMapper.toUserDto(user);
    }



    @Override
    public UserDto get(Long id) {
        if (id == null) throw new ValidationException("User ID cannot be null.");
        return UserMapper.toUserDto(userStorage.get(id)
                .orElseThrow(() -> new NotFoundException("User with ID #" + id + " does not exist.")));
    }

    @Override
    public void delete(Long id) {
        userStorage.get(id);
        userStorage.delete(id);
    }

    @Override
    public List<UserDto> getAll() {
        return userStorage.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    private void validate(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Invalid email: " + user.getEmail());
        }
    }

    private void checkEmailDuplicate(Long userId, String email) {
        if (getAll().stream()
                .filter(u -> !u.getId().equals(userId))
                .anyMatch(u -> u.getEmail().equals(email))) {
            throw new EmailException("User with email '" + email + "' already exists.");
        }
    }


}
