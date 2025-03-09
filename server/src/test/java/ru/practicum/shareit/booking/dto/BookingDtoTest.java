package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BookingDtoTest {

    @Test
    void testMapToBooking() {
        LocalDateTime now = LocalDateTime.now();
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .start(now.plusMinutes(1))
                .end(now.plusMinutes(2))
                .itemId(1L)
                .bookerId(1L)
                .status(BookingStatus.WAITING)
                .build();

        Item item = new Item();
        item.setId(1L);

        User booker = new User();
        booker.setId(1L);

        Booking booking = BookingMapper.mapToBooking(bookingDto, item, booker);

        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isEqualTo(bookingDto.getId());
        assertThat(booking.getStart()).isEqualTo(bookingDto.getStart());
        assertThat(booking.getEnd()).isEqualTo(bookingDto.getEnd());
        assertThat(booking.getItem()).isEqualTo(item);
        assertThat(booking.getBooker()).isEqualTo(booker);
        assertThat(booking.getStatus()).isEqualTo(bookingDto.getStatus());
    }

    @Test
    void testMapToBookingDtoOut() {
        LocalDateTime now = LocalDateTime.now();
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Дрель");

        User booker = new User();
        booker.setId(1L);
        booker.setName("Вася");
        booker.setEmail("afafa@mail.ru");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(now.plusMinutes(1));
        booking.setEnd(now.plusMinutes(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        BookingObjectsDto bookingObjectsDto = BookingMapper.mapToBookingDtoOut(booking);

        assertThat(bookingObjectsDto).isNotNull();
        assertThat(bookingObjectsDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingObjectsDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingObjectsDto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingObjectsDto.getItem()).isEqualTo(ItemMapper.mapToItemDto(booking.getItem()));
        assertThat(bookingObjectsDto.getBooker()).isEqualTo(UserMapper.mapToUserDto(booking.getBooker()));
        assertThat(bookingObjectsDto.getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void testMapToBookingDtoItem() {
        LocalDateTime now = LocalDateTime.now();
        User booker = new User();
        booker.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(now.plusMinutes(1));
        booking.setEnd(now.plusMinutes(2));
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        BookingItemDto bookingItemDto = BookingMapper.mapToBookingDtoItem(booking);

        assertThat(bookingItemDto).isNotNull();
        assertThat(bookingItemDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingItemDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingItemDto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingItemDto.getBookerId()).isEqualTo(booking.getBooker().getId());
        assertThat(bookingItemDto.getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void testMapToBookingDtoItemList() {
        LocalDateTime now = LocalDateTime.now();
        User booker = new User();
        booker.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(now.plusMinutes(1));
        booking.setEnd(now.plusMinutes(2));
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        List<Booking> bookings = Collections.singletonList(booking);

        List<BookingItemDto> bookingItemDtos = BookingMapper.mapToBookingDtoItem(bookings);

        assertThat(bookingItemDtos).isNotNull().hasSize(1);
        assertThat(bookingItemDtos.get(0)).isEqualTo(BookingMapper.mapToBookingDtoItem(booking));
    }

    @Test
    void testMapToBookingDtoOutList() {
        LocalDateTime now = LocalDateTime.now();
        Item item = new Item();
        item.setId(1L);
        item.setName("Дрель");
        item.setDescription("Дрель");

        User booker = new User();
        booker.setId(1L);
        booker.setName("Вася");
        booker.setEmail("afafa@mail.ru");

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(now.plusMinutes(1));
        booking.setEnd(now.plusMinutes(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        List<Booking> bookings = Collections.singletonList(booking);

        List<BookingObjectsDto> bookingObjectsDtos = BookingMapper.mapToBookingDtoOut(bookings);

        assertThat(bookingObjectsDtos).isNotNull().hasSize(1);
        assertThat(bookingObjectsDtos.get(0)).isEqualTo(BookingMapper.mapToBookingDtoOut(booking));
    }

    @Test
    void testMapToBookingDto() {
        LocalDateTime now = LocalDateTime.now();
        Item item = new Item();
        item.setId(1L);

        User booker = new User();
        booker.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(now.plusMinutes(1));
        booking.setEnd(now.plusMinutes(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        BookingDto bookingDto = BookingMapper.mapToBookingDto(booking);

        assertThat(bookingDto).isNotNull();
        assertThat(bookingDto.getId()).isEqualTo(booking.getId());
        assertThat(bookingDto.getStart()).isEqualTo(booking.getStart());
        assertThat(bookingDto.getEnd()).isEqualTo(booking.getEnd());
        assertThat(bookingDto.getItemId()).isEqualTo(booking.getItem().getId());
        assertThat(bookingDto.getBookerId()).isEqualTo(booking.getBooker().getId());
        assertThat(bookingDto.getStatus()).isEqualTo(booking.getStatus());
    }

    @Test
    void testMapToBookingDtoList() {
        LocalDateTime now = LocalDateTime.now();
        Item item = new Item();
        item.setId(1L);

        User booker = new User();
        booker.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(now.plusMinutes(1));
        booking.setEnd(now.plusMinutes(2));
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        List<Booking> bookings = Collections.singletonList(booking);

        List<BookingDto> bookingDtos = BookingMapper.mapToBookingDto(bookings);

        assertThat(bookingDtos).isNotNull().hasSize(1);
        assertThat(bookingDtos.get(0)).isEqualTo(BookingMapper.mapToBookingDto(booking));
    }
}