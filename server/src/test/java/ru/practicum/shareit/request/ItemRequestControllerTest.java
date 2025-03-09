package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestControllerTest {

    @Mock
    private ItemRequestService itemRequestService;

    @InjectMocks
    private ItemRequestController itemRequestController;

    private MockMvc mockMvc;
    private ItemRequestDto itemRequestDto;
    private ItemRequestItemsDto itemRequestItemsDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(itemRequestController).build();

        itemRequestDto = ItemRequestDto.builder()
                .description("Request description")
                .build();

        itemRequestItemsDto = ItemRequestItemsDto.builder()
                .description("Request description")
                .build();
    }

    @Test
    void create_ShouldReturnItemRequestDto_WhenCreated() throws Exception {
        when(itemRequestService.create(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(itemRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Request description"));

        verify(itemRequestService, times(1)).create(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    void getUserItemsReq_ShouldReturnListOfItemRequestItemsDto() throws Exception {
        List<ItemRequestItemsDto> itemRequests = Arrays.asList(itemRequestItemsDto);

        when(itemRequestService.getUserItemsReq(anyLong())).thenReturn(itemRequests);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Request description"));

        verify(itemRequestService, times(1)).getUserItemsReq(anyLong());
    }

    @Test
    void getItems_ShouldReturnListOfItemRequestItemsDto() throws Exception {
        List<ItemRequestItemsDto> itemRequests = Arrays.asList(itemRequestItemsDto);

        when(itemRequestService.getItems(anyLong(), anyInt(), anyInt())).thenReturn(itemRequests);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value("Request description"));

        verify(itemRequestService, times(1)).getItems(anyLong(), anyInt(), anyInt());
    }

    @Test
    void getItem_ShouldReturnItemRequestItemsDto_WhenFound() throws Exception {
        when(itemRequestService.getItem(anyLong(), anyLong())).thenReturn(itemRequestItemsDto);

        mockMvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Request description"));

        verify(itemRequestService, times(1)).getItem(anyLong(), anyLong());
    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
