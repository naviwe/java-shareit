package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.BookerInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Transactional
    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        validate(itemDto);
        var user = UserMapper.toUser(userService.get(userId));
        var item = ItemMapper.toItem(itemDto, UserMapper.toUserDto(user));
        item.setOwner(user);
        var savedItem = itemRepository.save(item);
        return ItemMapper.toItemDto(savedItem);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemDto getByItemIdAndUserId(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with ID #" + itemId + " does not exist."));

        List<CommentDto> comments = getAllCommentsByItemId(itemId);
        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            return ItemMapper.toItemDto(item, getLastBooking(item), getNextBooking(item), comments);
        }
        return ItemMapper.toItemDto(item, comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAll(Long userId) {
        userService.get(userId);
        return itemRepository.findAllByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(item -> ItemMapper.toItemDto(item, getLastBooking(item), getNextBooking(item), getAllCommentsByItemId(item.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllByText(String text) {
        return text.isBlank() ? List.of() : ItemMapper.itemlistToitemdtolist(itemRepository.search(text));
    }

    @Transactional
    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with ID #" + itemId + " does not exist."));

        if (!Objects.equals(existingItem.getOwner().getId(), userId)) {
            throw new NotFoundException("User with ID #" + userId + " is not the owner of the item.");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(existingItem));
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentDto commentDto, UserDto userDto, ItemDto itemDto) {
        List<Booking> bookings = bookingRepository.getAllUserBookings(userDto.getId(), itemDto.getId(), LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new ValidationException("To leave a comment, you must first make a booking.");
        }
        Comment comment = CommentMapper.toComment(commentDto, userDto, itemDto);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    private BookerInfoDto getLastBooking(Item item) {
        return bookingRepository.getLastBooking(item.getId(), LocalDateTime.now())
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }

    private BookerInfoDto getNextBooking(Item item) {
        return bookingRepository.getNextBooking(item.getId(), LocalDateTime.now())
                .map(BookingMapper::toBookingInfoDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> getAllCommentsByItemId(Long itemId) {
        return commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    private void validate(ItemDto item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Name cannot be blank");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Description cannot be blank");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Available cannot be null");
        }
    }
}
