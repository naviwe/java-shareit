package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingObjectsDto;

import java.util.List;

public interface BookingService {
    BookingObjectsDto createBooking(Long userId, BookingDto bookingDto);


    BookingObjectsDto confirmation(Long userId, boolean approved, Long bookingId);


    BookingObjectsDto getOnlyOwnerOrBooker(Long userId, Long bookingId);

    List<BookingObjectsDto> getListOfUserBooker(Long userId, BookingState state);

    List<BookingObjectsDto> getListBookerOfOwnerItems(Long userId, BookingState state);

}