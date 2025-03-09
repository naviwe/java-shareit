package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingObjectsDto;
import ru.practicum.shareit.booking.enums.BookingState;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    private static final String URL = "/bookings";

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private BookingObjectsDto bookingOutDto;

    private BookingDto.BookingDtoBuilder builderIn;
    private BookingObjectsDto.BookingObjectsDtoBuilder builderOut;

    @BeforeEach
    void setupBuilder() {
        LocalDateTime now = LocalDateTime.now();
        UserDto.UserDtoBuilder userDtoBuilder = UserDto.builder()
                .id(1L)
                .name("artem")
                .email("artemka@mail.ru");
        ItemDto.ItemDtoBuilder itemDtoBuilder = ItemDto.builder()
                .id(1L)
                .name("Hammer")
                .description("desc")
                .available(true);
        builderIn = BookingDto.builder()
                .itemId(1L)
                .start(now.plusMinutes(1))
                .end(now.plusMinutes(2));
        builderOut = BookingObjectsDto.builder()
                .id(1L)
                .booker(userDtoBuilder.build())
                .item(itemDtoBuilder.build())
                .start(now.plusMinutes(1))
                .end(now.plusMinutes(2))
                .status(BookingStatus.WAITING);
    }

    @Test
    void testCreateStandard() throws Exception {
        BookingDto bookingInDto = builderIn.build();
        bookingOutDto = builderOut.build();
        String json = mapper.writeValueAsString(bookingInDto);
        when(bookingService.createBooking(1L, bookingInDto)).thenReturn(bookingOutDto);
        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.status", is(bookingOutDto.getStatus().toString()), String.class));
    }

    @Test
    void testCreateFailItemIdNotFound() throws Exception {
        String error = String.format("Вещь с id %d не найдена", 99);
        BookingDto bookingInDto = builderIn.itemId(99L).build();
        String json = mapper.writeValueAsString(bookingInDto);
        when(bookingService.createBooking(1L, bookingInDto)).thenThrow(new NotFoundException(error));
        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(error), String.class));
    }

    @Test
    void testCreateFailItemIdNull() throws Exception {
        BookingDto bookingInDto = builderIn.itemId(null).build();
        String json = mapper.writeValueAsString(bookingInDto);
        String error = "Отсутствует itemId";
        when(mvc.perform(post(URL).header("X-Sharer-User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON).content(json)))
                .thenThrow(new ValidationException("Отсутствует itemId"));

        mvc.perform(post(URL)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", notNullValue()))
                .andExpect(jsonPath("$.error", containsString(error), String.class));
    }

    @Test
    void testConfirmationStandard() throws Exception {
        bookingOutDto = builderOut.status(BookingStatus.APPROVED).build();
        when(bookingService.confirmation(1L, true, 1L)).thenReturn(bookingOutDto);
        mvc.perform(patch(URL + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.start",
                        containsString(String.valueOf(bookingOutDto.getStart().getSecond())), String.class))
                .andExpect(jsonPath("$.status", is(bookingOutDto.getStatus().toString()), String.class));
    }

    @Test
    void testConfirmationStandardFalse() throws Exception {
        bookingOutDto = builderOut.status(BookingStatus.REJECTED).build();
        when(bookingService.confirmation(1L, false, 1L)).thenReturn(bookingOutDto);
        mvc.perform(patch(URL + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "false"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.start", containsString(String.valueOf(
                        bookingOutDto.getStart().getSecond())), String.class))
                .andExpect(jsonPath("$.status", is(bookingOutDto.getStatus().toString()), String.class));
    }

    @Test
    void testConfirmationFailRepeate() throws Exception {
        String error = String.format("Бронирование с id %d уже отклонено", 1);
        when(bookingService.confirmation(1L, false, 1L)).thenThrow(new ValidationException(error));
        mvc.perform(patch(URL + "/1")
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "false"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", containsString(error), String.class));
    }

    @Test
    void testGetOnlyOwnerOrBookerStandard() throws Exception {
        bookingOutDto = builderOut.build();
        when(bookingService.getOnlyOwnerOrBooker(1L, 1L)).thenReturn(bookingOutDto);
        mvc.perform(get(URL + "/1")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(bookingOutDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.start", containsString(String.valueOf(
                        bookingOutDto.getStart().getSecond())), String.class))
                .andExpect(jsonPath("$.status", is(bookingOutDto.getStatus().toString()), String.class));
    }

    @Test
    void testGetOnlyOwnerOrBookerFailUserId() throws Exception {
        String error = String.format("Пользователь с id %d не найден", -1);
        when(bookingService.getOnlyOwnerOrBooker(-1L, 1L)).thenThrow(new NotFoundException(error));
        mvc.perform(get(URL + "/1")
                        .header("X-Sharer-User-Id", -1))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(error), String.class));
    }

    @Test
    void testGetOnlyOwnerOrBookerFailBookerId() throws Exception {
        String error = String.format("Бронирование с id %d не найдено", 99);
        when(bookingService.getOnlyOwnerOrBooker(1L, 99L)).thenThrow(new NotFoundException(error));
        mvc.perform(get(URL + "/99")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is(error), String.class));
    }

    @Test
    void testGetListOfUserBookerEmpty() throws Exception {
        when(bookingService.getListOfUserBooker(1L, BookingState.REJECTED))
                .thenReturn(Collections.emptyList());
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "REJECTED"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetListOfUserBookerOneObject() throws Exception {
        bookingOutDto = builderOut.build();
        when(bookingService.getListOfUserBooker(1L, BookingState.WAITING))
                .thenReturn(List.of(bookingOutDto));
        mvc.perform(get(URL)
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "WAITING")
                        .param("from", "0")
                        .param("size", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingOutDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].status", is(bookingOutDto.getStatus().toString()), String.class));
    }

    @Test
    void testGetListBookerOfOwnerItemsEmptyList() throws Exception {
        when(bookingService.getListBookerOfOwnerItems(1L, BookingState.REJECTED))
                .thenReturn(Collections.emptyList());
        mvc.perform(get(URL + "/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "REJECTED"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void testGetListBookerOfOwnerItemsOneObject() throws Exception {
        bookingOutDto = builderOut.build();
        when(bookingService.getListBookerOfOwnerItems(1L, BookingState.WAITING))
                .thenReturn(List.of(bookingOutDto));
        mvc.perform(get(URL + "/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "WAITING")
                        .param("from", "0")
                        .param("size", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(bookingOutDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingOutDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].booker.id", is(bookingOutDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].start", containsString(String.valueOf(
                        bookingOutDto.getStart().getSecond())), String.class))
                .andExpect(jsonPath("$[0].status", is(bookingOutDto.getStatus().toString()), String.class));
    }
}