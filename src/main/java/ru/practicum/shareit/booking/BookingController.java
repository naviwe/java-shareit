package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
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
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingObjectsDto createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                           @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingObjectsDto confirmation(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @RequestParam("approved") boolean approved,
                                          @PathVariable("bookingId") Long bookingId) {
        return bookingService.confirmation(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public BookingObjectsDto getOnlyOwnerOrBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @PathVariable("bookingId") Long bookingId) {
        return bookingService.getOnlyOwnerOrBooker(userId, bookingId);
    }

    @GetMapping
    public List<BookingObjectsDto> getListOfUserBooker(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(name = "state", required = false, defaultValue = "ALL")
                                                       BookingState state) {
        return bookingService.getListOfUserBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingObjectsDto> getListBookerOfOwnerItems(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(name = "state", required = false, defaultValue = "ALL")
            BookingState state) {
        return bookingService.getListBookerOfOwnerItems(userId, state);
    }
}
