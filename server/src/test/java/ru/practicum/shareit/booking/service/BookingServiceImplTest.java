package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingObjectsDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.error.AccessDeniedException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private Item item;
    private BookingDto bookingDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);

        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(1L)
                .bookerId(1L)
                .status(BookingStatus.WAITING)
                .build();

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
    }

    @Test
    void testCreateBooking_Success() {
        User owner = new User();
        owner.setId(2L);
        item.setOwner(owner);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.isAvailbleTime(anyLong(), any(), any())).thenReturn(Collections.emptyList());
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingObjectsDto result = bookingService.createBooking(1L, bookingDto);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStatus(), result.getStatus());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void testCreateBooking_InvalidDates() {
        bookingDto.setStart(LocalDateTime.now().plusHours(2));
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(1L, bookingDto);
        });

        assertEquals("Дата начала бронирования должна быть до даты окончания", exception.getMessage());
    }

    @Test
    void testCreateBooking_ItemNotAvailable() {
        item.setAvailable(false);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.createBooking(1L, bookingDto);
        });

        assertEquals("Вещь недоступна для бронирования", exception.getMessage());
    }

    @Test
    void testCreateBooking_OwnerBooksOwnItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(1L, bookingDto);
        });

        assertEquals("Пользователь с ID 1 не может забронировать свою же вещь (ID 1)", exception.getMessage());
    }

    @Test
    void testConfirmation_SuccessApproved() {
        when(bookingRepository.getBookerWithAll(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingObjectsDto result = bookingService.confirmation(1L, true, 1L);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void testConfirmation_SuccessRejected() {
        when(bookingRepository.getBookerWithAll(anyLong())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingObjectsDto result = bookingService.confirmation(1L, false, 1L);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void testConfirmation_BookingNotFound() {
        when(bookingRepository.getBookerWithAll(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.confirmation(1L, true, 1L);
        });

        assertEquals("Booking не был найден по id = 1", exception.getMessage());
    }

    @Test
    void testConfirmation_AccessDenied() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        item.setOwner(anotherUser);

        when(bookingRepository.getBookerWithAll(anyLong())).thenReturn(Optional.of(booking));

        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            bookingService.confirmation(1L, true, 1L);
        });

        assertEquals("User id не является владельцем вещи", exception.getMessage());
    }

    @Test
    void testGetOnlyOwnerOrBooker_Success() {
        when(bookingRepository.getByOwnerIdOrBookerId(anyLong(), anyLong())).thenReturn(Optional.of(booking));

        BookingObjectsDto result = bookingService.getOnlyOwnerOrBooker(1L, 1L);

        assertNotNull(result);
        assertEquals(booking.getId(), result.getId());
    }

    @Test
    void testGetOnlyOwnerOrBooker_NotFound() {
        when(bookingRepository.getByOwnerIdOrBookerId(anyLong(), anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            bookingService.getOnlyOwnerOrBooker(1L, 1L);
        });

        assertEquals("Бронирование не найдено, возможно вы не её владелец/создатель", exception.getMessage());
    }

    @Test
    void testGetListOfUserBooker_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        when(bookingRepository.findAllByBookerIdOrderByStartDesc(anyLong())).thenReturn(Collections.singletonList(booking));

        List<BookingObjectsDto> result = bookingService.getListOfUserBooker(1L, BookingState.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetListBookerOfOwnerItems_Success() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(bookingRepository.findByItemOwnerIdOrderByStartDesc(anyLong())).thenReturn(Collections.singletonList(booking));

        List<BookingObjectsDto> result = bookingService.getListBookerOfOwnerItems(1L, BookingState.ALL);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}