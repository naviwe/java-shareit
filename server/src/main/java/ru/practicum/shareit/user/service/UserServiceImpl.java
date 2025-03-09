package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.EmailException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getList() {
        return UserMapper.mapToUserDto(userRepository.findAll());
    }

    @Override
    public UserDto create(UserDto userDto) {
        repeatCheck(userDto);
        return UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUser(userDto)));
    }

    @Override
    public void deleteById(long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto getUserById(long userId) {
        return UserMapper.mapToUserDto(userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден по id = " + userId)
        ));
    }

    @Override
    public UserDto updateUser(long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Пользователь не найден по id = " + userId));
        boolean isHasName = userDto.getName() != null;
        boolean isHasEmail = userDto.getEmail() != null;

        if (isHasName) user.setName(userDto.getName());
        if (isHasEmail) user.setEmail(userDto.getEmail());

        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    private void repeatCheck(UserDto userDto) {
        List<UserDto> userDtos = UserMapper.mapToUserDto(userRepository.findAll());
        userDtos.forEach(user -> {
            if (user.getEmail().equals(userDto.getEmail())) {
                throw new EmailException(String.format("Пользователь %s уже существует", userDto));
            }
        });
    }

    private void repeatCheckWithId(UserDto userDto, long userId) {
        List<UserDto> userDtos = UserMapper.mapToUserDto(userRepository.findAll());
        userDtos.forEach(user -> {
            if (user.getEmail().equals(userDto.getEmail())
                    && user.getId() != userId) {
                throw new EmailException(String.format("Пользователь %s уже существует", userDto));
            }
        });
    }
}