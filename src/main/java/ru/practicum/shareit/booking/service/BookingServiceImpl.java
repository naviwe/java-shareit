package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.AccessDeniedException;
import ru.practicum.shareit.error.IncorrectStateException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingOutputDto create(UserDto userDto, ItemDto itemDto, BookingInputDto bookingInputDto) {
        if (!itemDto.getAvailable()) {
            throw new ValidationException("Item is not available for booking");
        }

        if (bookingInputDto.getEnd().isBefore(bookingInputDto.getStart())) {
            throw new ValidationException("End date cannot be earlier than the start date");
        }

        if (bookingInputDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start date cannot be in the past");
        }

        if (Objects.equals(itemDto.getOwnerId(), userDto.getId())) {
            throw new NotFoundException("Item not found");
        }

        Booking booking = BookingMapper.toBooking(bookingInputDto, BookingStatus.WAITING, itemDto, userDto);

        return BookingMapper.toBookingCreatedDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingOutputDto approveByOwner(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new AccessDeniedException(String.format("Booking with id %s not found", bookingId));
        });

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new AccessDeniedException("User does not own this item");
        }

        if (booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new AccessDeniedException("Status already set");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        return BookingMapper.toBookingCreatedDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingOutputDto getBookingByIdAndUser(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> {
            throw new NotFoundException(String.format("Booking with id %s not found", bookingId));
        });

        if (!Objects.equals(booking.getBooker().getId(), userId)
                && !Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("Booking not found");
        }
        return BookingMapper.toBookingCreatedDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingOutputDto> findAllByBooker(Long bookerId, State state) {
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return BookingMapper
                        .toBookingCreatedDto(bookingRepository
                                .getAllBookingsByBookerId(bookerId));
            case CURRENT:
                return BookingMapper
                        .toBookingCreatedDto(bookingRepository
                                .getAllCurrentBookingsByBookerId(bookerId, now));
            case WAITING:
                return BookingMapper
                        .toBookingCreatedDto(bookingRepository
                                .getAllWaitingBookingsByBookerId(bookerId, now));
            case PAST:
                return BookingMapper
                        .toBookingCreatedDto(bookingRepository
                                .getAllPastBookingsByBookerId(bookerId, now));
            case FUTURE:
                return BookingMapper
                        .toBookingCreatedDto(bookingRepository
                                .getAllFutureBookingsByBookerId(bookerId, now));
            case REJECTED:
                return BookingMapper
                        .toBookingCreatedDto(bookingRepository
                                .getAllRejectedBookingsByBookerId(bookerId));
            default:
                throw new IncorrectStateException("Unknown state: " + state);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingOutputDto> findAllByOwner(Long userId, State state) {
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case ALL:
                return BookingMapper
                        .toBookingCreatedDto(bookingRepository
                                .getAllBookingsByOwnerId(userId));
            case CURRENT:
                return BookingMapper
                        .toBookingCreatedDto(bookingRepository
                                .getAllCurrentBookingsByOwnerId(userId, now));
            case WAITING:
                return BookingMapper
                        .toBookingCreatedDto(bookingRepository
                                .getAllWaitingBookingsByOwnerId(userId, now));
            case PAST:
                return BookingMapper
                        .toBookingCreatedDto(bookingRepository
                                .getAllPastBookingsByOwnerId(userId, now));
            case FUTURE:
                return BookingMapper
                        .toBookingCreatedDto(bookingRepository
                                .getAllFutureBookingsByOwnerId(userId, now));
            case REJECTED:
                return BookingMapper
                        .toBookingCreatedDto(bookingRepository
                                .getAllRejectedBookingsByOwnerId(userId));
            default:
                throw new IncorrectStateException("Unknown state: " + state);
        }
    }
}
