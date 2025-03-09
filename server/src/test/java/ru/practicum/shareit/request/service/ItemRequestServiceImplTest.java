package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemsDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private ItemRequestDto itemRequestDto;
    private ItemRequestItemsDto itemRequestItemsDto;
    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("User");
        user.setEmail("user@example.com");

        itemRequestDto = ItemRequestDto.builder()
                .description("Request description")
                .build();

        itemRequestItemsDto = ItemRequestItemsDto.builder()
                .description("Request description")
                .build();

        itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Request description");
        itemRequest.setRequestor(user);
    }

    @Test
    void create_ShouldReturnItemRequestDto_WhenValid() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto result = itemRequestService.create(1L, itemRequestDto);

        assertNotNull(result);
        assertEquals("Request description", result.getDescription());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void create_ShouldThrowValidationException_WhenDescriptionIsEmpty() {
        ItemRequestDto invalidDto = ItemRequestDto.builder().description("").build();

        assertThrows(ValidationException.class, () -> itemRequestService.create(1L, invalidDto));
    }

    @Test
    void getUserItemsReq_ShouldReturnListOfItemRequestItemsDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findByRequestorId(anyLong())).thenReturn(Arrays.asList(itemRequest));

        List<ItemRequestItemsDto> result = itemRequestService.getUserItemsReq(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(itemRequestRepository, times(1)).findByRequestorId(anyLong());
    }

    @Test
    void getItems_ShouldReturnListOfItemRequestItemsDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        Page<ItemRequest> mockPage = new PageImpl<>(Arrays.asList(itemRequest));
        when(itemRequestRepository.findByRequestorIdNot(anyLong(), any(Pageable.class))).thenReturn(mockPage);

        List<ItemRequestItemsDto> result = itemRequestService.getItems(1L, 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(itemRequestRepository, times(1)).findByRequestorIdNot(anyLong(), any(Pageable.class));
    }

    @Test
    void getItem_ShouldReturnItemRequestItemsDto_WhenFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findByRequestId(anyLong())).thenReturn(Arrays.asList(new Item()));

        ItemRequestItemsDto result = itemRequestService.getItem(1L, 1L);

        assertNotNull(result);
        assertEquals("Request description", result.getDescription());
        verify(itemRequestRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findByRequestId(anyLong());
    }

    @Test
    void getItem_ShouldThrowNotFoundException_WhenRequestNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getItem(1L, 1L));
    }
}
