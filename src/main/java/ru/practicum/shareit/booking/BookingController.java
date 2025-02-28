package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    @PostMapping
    public BookingOutputDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestBody BookingInputDto bookingInputDto) {
        log.info("Creating booking: User ID: {}, Item ID: {}, Start Date: {}, End Date: {}",
                userId, bookingInputDto.getItemId(), bookingInputDto.getStart(), bookingInputDto.getEnd());

        UserDto userDto = userService.get(userId);
        ItemDto itemDto = ItemMapper.toItemDto(itemService.findById(bookingInputDto.getItemId(), userId), userId);

        BookingOutputDto bookingOutputDto = bookingService.create(userDto, itemDto, bookingInputDto);
        log.info("Booking created successfully: {}", bookingOutputDto);
        return bookingOutputDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto approveByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam("approved") Boolean approved,
                                           @PathVariable("bookingId") Long bookingId) {
        log.info("Approving booking: Booking ID: {}, User ID: {}, Approved: {}", bookingId, userId, approved);

        BookingOutputDto bookingOutputDto = bookingService.approveByOwner(userId, bookingId, approved);
        log.info("Booking approval status updated: {}", bookingOutputDto);
        return bookingOutputDto;
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable("bookingId") Long bookingId) {
        log.info("Fetching booking details: Booking ID: {}, User ID: {}", bookingId, userId);

        BookingOutputDto bookingOutputDto = bookingService.getBookingByIdAndUser(bookingId, userId);
        log.info("Booking details retrieved: {}", bookingOutputDto);
        return bookingOutputDto;
    }

    @GetMapping
    public List<BookingOutputDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(value = "state", defaultValue = "ALL") State state) {
        log.info("Fetching all bookings for User ID: {} with state: {}", userId, state);

        UserDto userDto = userService.get(userId);
        List<BookingOutputDto> bookingOutputDtos = bookingService.findAllByBooker(userDto.getId(), state);
        log.info("Bookings retrieved: {}", bookingOutputDtos.size());
        return bookingOutputDtos;
    }

    @GetMapping("/owner")
    public List<BookingOutputDto> getAllByOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(value = "state", defaultValue = "ALL") State state) {
        log.info("Fetching all bookings for Owner ID: {} with state: {}", userId, state);

        UserDto userDto = userService.get(userId);
        List<BookingOutputDto> bookingOutputDtos = bookingService.findAllByOwner(userDto.getId(), state);
        log.info("Bookings for owner retrieved: {}", bookingOutputDtos.size());
        return bookingOutputDtos;
    }
}
