package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemWithCommentDto> getItemsByUserId(long userId) {
        List<Item> items = itemRepository.findItemsByOwnerId(userId);
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Long, ItemWithCommentDto> itemsWithIds = new HashMap<>();
        items.forEach(item -> itemsWithIds.put(item.getId(), ItemMapper.mapToItemWitchCommentDto(item)));
        addBookingDatesToItems(itemsWithIds);
        addBookingDatesToItems(itemsWithIds);
        return new ArrayList<>(itemsWithIds.values());
    }

    @Override
    public ItemDto create(long userId, ItemDto itemDto) {
        boolean isNullAvailable = itemDto.getAvailable() == null;
        boolean isNullName = itemDto.getName() == null || itemDto.getName().isBlank();
        boolean isNullDescription = itemDto.getDescription() == null;

        if (isNullAvailable) throw new ValidationException("ERROR: Available is null");
        if (isNullName) throw new ValidationException("ERROR: Name is null");
        if (isNullDescription) throw new ValidationException("ERROR: Description is null");

        Item item = itemRepository.save(ItemMapper.mapToItem(itemDto,
                userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User не найден по id = " + userId))));
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public void deleteByUserIdAndItemId(long userId, long itemId) {
        itemRepository.deleteByIdAndOwnerId(userId, itemId);
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = itemRepository.findByIdAndOwnerId(itemId, userId)
                .orElseThrow(() -> new NotFoundException("Item не был найден"));
        boolean isHasName = itemDto.getName() != null;
        boolean isHasDescription = itemDto.getDescription() != null;
        boolean isHasAvailable = itemDto.getAvailable() != null;

        if (isHasAvailable) item.setAvailable(itemDto.getAvailable());
        if (isHasName) item.setName(itemDto.getName());
        if (isHasDescription) item.setDescription(itemDto.getDescription());
        item = itemRepository.save(item);
        return ItemMapper.mapToItemDto(item);
    }

    @Override
    public ItemWithCommentDto getItem(long userId, long itemId) {
        Item item = itemRepository.findByIdFetch(itemId)
                .orElseThrow(() -> new NotFoundException("Item не найден по id = " + itemId));
        ItemWithCommentDto itemWithCommentDto = ItemMapper.mapToItemWitchCommentDto(item);

        long ownerId = item.getOwner().getId();
        if (ownerId == userId) {
            addBookingsToItem(itemWithCommentDto);
        }
        addCommentsToItem(itemWithCommentDto);
        return itemWithCommentDto;
    }

    @Override
    public List<ItemDto> getAll() {
        List<Item> items = itemRepository.findAll();
        return ItemMapper.mapToItemDto(items);
    }

    @Override
    public List<ItemDto> searchByText(String text, long userId) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.getListILikeByText(text.toLowerCase());
        return ItemMapper.mapToItemDto(items);
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User не найден по id = " + userId));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item не найден по id = " + itemId));

        List<Booking> bookingsItemByUser = bookingRepository
                .findByBookerIdAndItemIdAndStatusAndStartIsBefore(userId, itemId, BookingStatus.APPROVED, LocalDateTime.now());
        if (bookingsItemByUser.isEmpty()) {
            throw new ValidationException(
                    String.format("User с id %s не арендовал вещь с id %s", userId, itemId));
        }
        if (commentDto.getText().isBlank()) throw new IllegalArgumentException("Text не может быть пустым");
        Comment comment = CommentMapper.mapToComment(commentDto, owner, item);

        return CommentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    private void addBookingDatesToItems(Map<Long, ItemWithCommentDto> itemsWithId) {
        Map<Long, List<Booking>> bookings = new HashMap<>();
        List<Long> itemIds = new ArrayList<>(itemsWithId.keySet());

        List<Booking> bookingsList = bookingRepository.findByItemIdInAndStatusOrStatusOrderByStartAsc(itemIds,
                BookingStatus.APPROVED, BookingStatus.WAITING);

        bookingsList.forEach(booking -> bookings.computeIfAbsent(booking.getItem().getId(),
                key -> new ArrayList<>()).add(booking));

        bookings.forEach((key, value) -> lastNextBooking(value, itemsWithId.get(key)));
    }

    private void addBookingsToItem(ItemWithCommentDto itemDto) {
        List<Booking> bookings = bookingRepository.findByItemIdAndStatusOrStatusOrderByStartAsc(itemDto.getId(),
                BookingStatus.APPROVED, BookingStatus.WAITING);
        if (bookings.isEmpty()) {
            return;
        }
        lastNextBooking(bookings, itemDto);
    }

    private void addCommentsToItem(ItemWithCommentDto itemDto) {
        commentRepository.findAllByItemIdOrderByCreatedDesc(itemDto.getId())
                .forEach(comment -> itemDto.addComment(CommentMapper.mapToCommentDto(comment)));
    }

    private void lastNextBooking(List<Booking> bookings, ItemWithCommentDto itemDto) {

        Booking lastBooking;
        Booking nextBooking = null;
        LocalDateTime now = LocalDateTime.now();


        if (bookings.get(0).getStart().isAfter(now)) {
            itemDto.setNextBooking(BookingMapper.mapToBookingDtoItem(bookings.get(0)));
            return;
        } else {
            lastBooking = bookings.get(0);
        }

        for (int i = 1; i < bookings.size(); i++) {
            if (bookings.get(i).getStart().isAfter(now)) {
                lastBooking = bookings.get(i - 1);
                nextBooking = bookings.get(i);
                break;
            }
        }
        itemDto.setLastBooking(BookingMapper.mapToBookingDtoItem(lastBooking));
        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.mapToBookingDtoItem(nextBooking));
        }
    }
}
