package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.error.EmailException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        userDto = UserMapper.mapToUserDto(user);
    }

    @Test
    void getList_ShouldReturnUserList() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getList();

        assertEquals(1, result.size());
        assertEquals(userDto, result.get(0));
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void create_ShouldSaveUser() {
        when(userRepository.findAll()).thenReturn(List.of());
        when(userRepository.save(any())).thenReturn(user);

        UserDto result = userService.create(userDto);

        assertNotNull(result);
        assertEquals(userDto, result);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void create_ShouldThrowEmailException_WhenEmailExists() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        assertThrows(EmailException.class, () -> userService.create(userDto));
    }

    @Test
    void deleteById_ShouldCallRepository() {
        doNothing().when(userRepository).deleteById(user.getId());

        userService.deleteById(user.getId());

        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    void getUserById_ShouldReturnUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(user.getId());

        assertNotNull(result);
        assertEquals(userDto, result);
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getUserById_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void updateUser_ShouldUpdateUser() {
        UserDto updatedUserDto = UserDto.builder()
                .id(user.getId())
                .name("Jane Doe")
                .email("jane.doe@example.com")
                .build();

        User updatedUser = new User();
        updatedUser.setId(user.getId());
        updatedUser.setName("Jane Doe");
        updatedUser.setEmail("jane.doe@example.com");


        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(updatedUser);

        UserDto result = userService.updateUser(user.getId(), updatedUserDto);

        assertEquals(updatedUserDto, result);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void updateUser_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(999L, userDto));
    }
}
