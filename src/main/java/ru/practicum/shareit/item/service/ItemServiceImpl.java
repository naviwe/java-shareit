package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.LastNextBookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final UserRepository userRepository;

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

    @Transactional
    @Override
    public ItemOwnerDto findById(long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        Booking l = bookingRepository.getFirstByItemIdOrderByEndDesc(itemId);
        Booking n = bookingRepository.getFirstByItemIdOrderByStartAsc(itemId);
        LastNextBookingDto last = null;
        if (l != null) {
            last = BookingMapper.toLastNextBookingDto(l);
        }
        LastNextBookingDto next = null;
        if (n != null) {
            next = BookingMapper.toLastNextBookingDto(n);
        }
        List<CommentDto> comments = getComments(itemId);
        if (userId == item.getOwner().getId()) {
            return ItemMapper.toItemOwnerDto(item, comments, next, last);
        }
        return ItemMapper.toItemOwnerDto(item, comments, null, null);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemOwnerDto> getItems(long userId) {
        List<Item> userItems = itemRepository.findByOwnerId(userId);
        List<ItemOwnerDto> result = new ArrayList<>();
        for (Item item : userItems) {
            result.add(findById(userId, item.getId()));
        }
        return result;
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

    @SneakyThrows
    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (commentDto.getText().isEmpty()) {
            throw new ValidationException("Пустой комментарий");
        }
        bookingRepository.findByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now()).stream()
                .filter(booking -> booking.getItem().getId() == itemId)
                .findAny()
                .orElseThrow(() -> new ValidationException("Доступ запрещен"));
        Comment comment = commentRepository.save(CommentMapper.toComment(commentDto, item, user));
        return CommentMapper.toCommentDto(comment);
    }

    @Override
    public List<CommentDto> getComments(long itemId) {
        List<Comment> comments = commentRepository.getAllByItemId(itemId);
        return CommentMapper.toCommentDto(comments);
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
