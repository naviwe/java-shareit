package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingObjectsDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.error.AccessDeniedException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingObjectsDto createBooking(Long userId, BookingDto bookingDto) {
        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new ValidationException("Дата начала бронирования должна быть до даты окончания");
        }
        if (!getItem(bookingDto.getItemId()).isAvailable()) {
            throw new IllegalArgumentException("Вещь недоступна для бронирования");
        }

        User booker = getUser(userId);
        long itemId = bookingDto.getItemId();
        Item item = getItem(itemId);
        bookingDto.setStatus(BookingStatus.WAITING);

        if (isOwner(userId, item)) {
            throw new ValidationException(String.format(
                    "Пользователь с ID %d не может забронировать свою же вещь (ID %d)", userId, itemId));
        }

        List<Booking> bookings = bookingRepository.isAvailbleTime(itemId, bookingDto.getStart(), bookingDto.getEnd());
        if (!bookings.isEmpty()) {
            throw new ValidationException("Время для бронирования недоступно");
        }

        Booking booking = bookingRepository.save(BookingMapper.mapToBooking(
                bookingDto,
                item,
                booker));

        return BookingMapper.mapToBookingDtoOut(booking);
    }

    @Override
    @Transactional
    public BookingObjectsDto confirmation(Long userId, boolean approved, Long bookingId) {
        Booking booking = bookingRepository.getBookerWithAll(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking не был найден по id = " + bookingId));
        if (!booking.getItem().getOwner().getId().equals(userId)) throw new AccessDeniedException(
                "User id не является владельцем вещи");
        if (approved) {
            if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                throw new ValidationException(String.format("Бронирование с id %d уже подтверждено", bookingId));
            }
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            if (booking.getStatus().equals(BookingStatus.REJECTED)) {
                throw new ValidationException(String.format("Бронирование с id %d уже отклонено", bookingId));
            }
            booking.setStatus(BookingStatus.REJECTED);
        }
        booking = bookingRepository.save(booking);
        return BookingMapper.mapToBookingDtoOut(booking);
    }

    @Override
    public BookingObjectsDto getOnlyOwnerOrBooker(Long userId, Long bookingId) {
        Booking booking = bookingRepository.getByOwnerIdOrBookerId(userId, bookingId)
                .orElseThrow(() -> new NotFoundException(
                        "Бронирование не найдено, возможно вы не её владелец/создатель"));
        return BookingMapper.mapToBookingDtoOut(booking);
    }

    @Override
    public List<BookingObjectsDto> getListOfUserBooker(Long userId, BookingState state) {
        LocalDateTime time = LocalDateTime.now();
        getUser(userId);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, time, time);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, time);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId, time);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new RuntimeException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.mapToBookingDtoOut(bookings);
    }

    @Override
    public List<BookingObjectsDto> getListBookerOfOwnerItems(Long userId, BookingState state) {
        LocalDateTime time = LocalDateTime.now();
        getUser(userId);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId, time, time);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, time);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, time);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
                break;
            default:
                throw new RuntimeException("Unknown state: UNSUPPORTED_STATUS");
        }
        return BookingMapper.mapToBookingDtoOut(bookings);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User с id = %d не найден", userId)));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Item с id = %d не найден", itemId)));
    }

    private boolean isOwner(long userId, Item item) {
        long ownerId = item.getOwner().getId();
        return ownerId == userId;
    }
}
