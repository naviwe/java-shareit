package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
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
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("User Name");
        user.setEmail("user@example.com");

        item = new Item();
        item.setId(1L);
        item.setName("Item Name");
        item.setDescription("Item Description");
        item.setAvailable(true);
        item.setOwner(user);

        ItemDto itemDto = ItemDto.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Comment Text");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());

        CommentDto commentDto = CommentDto.builder()
                .text("Comment Text")
                .build();

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.APPROVED);
    }

    @Test
    void getItemsByUserId_ShouldReturnEmptyList_WhenNoItemsFound() {
        when(itemRepository.findItemsByOwnerId(anyLong())).thenReturn(Collections.emptyList());

        List<ItemWithCommentDto> result = itemService.getItemsByUserId(1L);

        assertTrue(result.isEmpty());
        verify(itemRepository, times(1)).findItemsByOwnerId(anyLong());
    }

    @Test
    void create_ShouldThrowValidationException_WhenAvailableIsNull() {
        itemDto = ItemDto.builder()
                .name("Item Name")
                .description("Item Description")
                .available(null)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.create(1L, itemDto));

        assertEquals("ERROR: Available is null", exception.getMessage());
    }

    @Test
    void create_ShouldThrowValidationException_WhenNameIsNull() {
        itemDto = ItemDto.builder()
                .name(null)
                .description("Item Description")
                .available(true)
                .build();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.create(1L, itemDto));

        assertEquals("ERROR: Name is null", exception.getMessage());
    }

    @Test
    void create_ShouldThrowValidationException_WhenDescriptionIsNull() {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        itemDto.setDescription(null);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.create(1L, itemDto));

        assertEquals("ERROR: Description is null", exception.getMessage());
    }


    @Test
    void create_ShouldReturnItemDto_WhenItemIsCreated() {
        ItemDto itemDto = ItemDto.builder()
                .name("Item Name")
                .description("Item Description")
                .available(true)
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.create(1L, itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).save(any(Item.class));
    }


    @Test
    void deleteByUserIdAndItemId_ShouldCallRepositoryDeleteMethod() {
        doNothing().when(itemRepository).deleteByIdAndOwnerId(anyLong(), anyLong());

        itemService.deleteByUserIdAndItemId(1L, 1L);

        verify(itemRepository, times(1)).deleteByIdAndOwnerId(anyLong(), anyLong());
    }

    @Test
    void updateItem_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.updateItem(1L, 1L, itemDto));

        assertEquals("Item не был найден", exception.getMessage());
    }

    @Test
    void updateItem_ShouldReturnUpdatedItemDto_WhenItemIsUpdated() {
        ItemDto itemDto = ItemDto.builder()
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();

        when(itemRepository.findByIdAndOwnerId(anyLong(), anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.updateItem(1L, 1L, itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());

        verify(itemRepository, times(1)).findByIdAndOwnerId(anyLong(), anyLong());
        verify(itemRepository, times(1)).save(any(Item.class));
    }


    @Test
    void getItem_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(itemRepository.findByIdFetch(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.getItem(1L, 1L));

        assertEquals("Item не найден по id = 1", exception.getMessage());
    }

    @Test
    void getItem_ShouldReturnItemWithCommentDto_WhenItemFound() {
        when(itemRepository.findByIdFetch(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndStatusOrStatusOrderByStartAsc(anyLong(), any(BookingStatus.class), any(BookingStatus.class)))
                .thenReturn(Collections.emptyList());
        when(commentRepository.findAllByItemIdOrderByCreatedDesc(anyLong())).thenReturn(Collections.emptyList());

        ItemWithCommentDto result = itemService.getItem(1L, 1L);

        assertNotNull(result);
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        verify(itemRepository, times(1)).findByIdFetch(anyLong());
        verify(bookingRepository, times(1)).findByItemIdAndStatusOrStatusOrderByStartAsc(anyLong(), any(BookingStatus.class), any(BookingStatus.class));
        verify(commentRepository, times(1)).findAllByItemIdOrderByCreatedDesc(anyLong());
    }

    @Test
    void getAll_ShouldReturnListOfItemDto() {
        when(itemRepository.findAll()).thenReturn(Collections.singletonList(item));

        List<ItemDto> result = itemService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void searchByText_ShouldReturnEmptyList_WhenTextIsBlank() {
        List<ItemDto> result = itemService.searchByText("", 1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void searchByText_ShouldReturnListOfItemDto_WhenTextIsNotBlank() {
        when(itemRepository.getListILikeByText(anyString())).thenReturn(Collections.singletonList(item));

        List<ItemDto> result = itemService.searchByText("text", 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(item.getName(), result.get(0).getName());
        assertEquals(item.getDescription(), result.get(0).getDescription());
        verify(itemRepository, times(1)).getListILikeByText(anyString());
    }

    @Test
    void createComment_ShouldThrowNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.createComment(1L, 1L, commentDto));

        assertEquals("User не найден по id = 1", exception.getMessage());
    }

    @Test
    void createComment_ShouldThrowNotFoundException_WhenItemNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> itemService.createComment(1L, 1L, commentDto));

        assertEquals("Item не найден по id = 1", exception.getMessage());
    }

    @Test
    void createComment_ShouldThrowValidationException_WhenUserDidNotBookItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndStartIsBefore(anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.createComment(1L, 1L, commentDto));

        assertEquals("User с id 1 не арендовал вещь с id 1", exception.getMessage());
    }

    @Test
    void createComment_ShouldThrowIllegalArgumentException_WhenTextIsBlank() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndStartIsBefore(anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(booking));

        commentDto = CommentDto.builder()
                .text("")
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> itemService.createComment(1L, 1L, commentDto));

        assertEquals("Text не может быть пустым", exception.getMessage());
    }

    @Test
    void createComment_ShouldReturnCommentDto_WhenCommentIsCreated() {
        commentDto = CommentDto.builder()
                .text("Comment Text")
                .build();

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findByBookerIdAndItemIdAndStatusAndStartIsBefore(anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(booking));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemService.createComment(1L, 1L, commentDto);

        assertNotNull(result);

        assertEquals(commentDto.getText(), result.getText());

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findByBookerIdAndItemIdAndStatusAndStartIsBefore(anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class));
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
}