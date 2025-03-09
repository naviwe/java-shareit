package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(itemController).build();
    }

    @Test
    void getAllItems() throws Exception {
        List<ItemWithCommentDto> items = Collections.singletonList(ItemWithCommentDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Description")
                .available(true)
                .build());
        when(itemService.getItemsByUserId(1L)).thenReturn(items);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Item 1"));

        verify(itemService, times(1)).getItemsByUserId(1L);
    }

    @Test
    void createItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Description")
                .available(true)
                .build();
        when(itemService.create(1L, itemDto)).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content("{\"name\":\"Item 1\", \"description\":\"Description\", \"available\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item 1"))
                .andExpect(jsonPath("$.description").value("Description"));

        verify(itemService, times(1)).create(1L, itemDto);
    }

    @Test
    void deleteItem() throws Exception {
        doNothing().when(itemService).deleteByUserIdAndItemId(1L, 1L);

        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        verify(itemService, times(1)).deleteByUserIdAndItemId(1L, 1L);
    }

    @Test
    void updateItem() throws Exception {
        ItemDto updatedItemDto = ItemDto.builder()
                .id(1L)
                .name("Updated Item")
                .description("Updated Description")
                .available(true)
                .build();
        when(itemService.updateItem(1L, 1L, updatedItemDto)).thenReturn(updatedItemDto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content("{\"name\":\"Updated Item\", \"description\":\"Updated Description\", \"available\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("Updated Description"));

        verify(itemService, times(1)).updateItem(1L, 1L, updatedItemDto);
    }

    @Test
    void getItem() throws Exception {
        ItemWithCommentDto item = ItemWithCommentDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Description")
                .available(true)
                .build();
        when(itemService.getItem(1L, 1L)).thenReturn(item);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Item 1"));

        verify(itemService, times(1)).getItem(1L, 1L);
    }

    @Test
    void searchItems() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item 1")
                .description("Description")
                .available(true)
                .build();
        when(itemService.searchByText("item", 1L)).thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Item 1"));

        verify(itemService, times(1)).searchByText("item", 1L);
    }

    @Test
    void createComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Great item")
                .created(LocalDateTime.parse("2025-03-09T12:00:00"))
                .build();
        when(itemService.createComment(1L, 1L, commentDto)).thenReturn(commentDto);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json")
                        .content("{\"text\":\"Great item\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Great item"));

        verify(itemService, times(1)).createComment(1L, 1L, commentDto);
    }
}
