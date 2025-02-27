package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface BookingService {
    BookingOutputDto create(UserDto userDto, ItemDto itemDto, BookingInputDto bookingInputDto);

    BookingOutputDto approveByOwner(Long userId, Long bookingId, Boolean approved);

    BookingOutputDto getBookingByIdAndUser(Long bookingId, Long userId);

    List<BookingOutputDto> findAllByBooker(Long bookerId, State state);

    List<BookingOutputDto> findAllByOwner(Long userId, State state);
}