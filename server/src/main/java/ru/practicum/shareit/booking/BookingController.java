package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingObjectsDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Component
@Validated
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingObjectsDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @Valid @RequestBody BookingDto bookingDto) {
        log.info("Создание бронирования для пользователя с ID: {}, данные бронирования: {}", userId, bookingDto);
        BookingObjectsDto result = bookingService.createBooking(userId, bookingDto);
        log.info("Бронирование успешно создано: {}", result);
        return result;
    }

    @PatchMapping("/{bookingId}")
    public BookingObjectsDto confirmation(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam("approved") boolean approved,
                                          @PathVariable("bookingId") Long bookingId) {
        log.info("Подтверждение бронирования с ID: {}, пользователь с ID: {}, одобрено: {}", bookingId, userId, approved);
        BookingObjectsDto result = bookingService.confirmation(userId, approved, bookingId);
        log.info("Результат подтверждения бронирования: {}", result);
        return result;
    }

    @GetMapping("/{bookingId}")
    public BookingObjectsDto getOnlyOwnerOrBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable("bookingId") Long bookingId) {
        log.info("Получение информации о бронировании с ID: {}, пользователь с ID: {}", bookingId, userId);
        BookingObjectsDto result = bookingService.getOnlyOwnerOrBooker(userId, bookingId);
        log.info("Информация о бронировании получена: {}", result);
        return result;
    }

    @GetMapping
    public List<BookingObjectsDto> getListOfUserBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(name = "state", required = false, defaultValue = "ALL")
                                                       BookingState state) {
        log.info("Получение списка бронирований для пользователя с ID: {}, состояние: {}", userId, state);
        List<BookingObjectsDto> result = bookingService.getListOfUserBooker(userId, state);
        log.info("Список бронирований получен, количество: {}", result.size());
        return result;
    }

    @GetMapping("/owner")
    public List<BookingObjectsDto> getListBookerOfOwnerItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL")
            BookingState state) {
        log.info("Получение списка бронирований для владельца с ID: {}, состояние: {}", userId, state);
        List<BookingObjectsDto> result = bookingService.getListBookerOfOwnerItems(userId, state);
        log.info("Список бронирований владельца получен, количество: {}", result.size());
        return result;
    }
}