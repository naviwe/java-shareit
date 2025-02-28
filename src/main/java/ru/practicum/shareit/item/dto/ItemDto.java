package ru.practicum.shareit.item.dto;

import lombok.Value;
import ru.practicum.shareit.booking.dto.LastNextBookingDto;

import java.util.List;

@Value
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long ownerId;
    LastNextBookingDto lastBooking;
    LastNextBookingDto nextBooking;
    List<CommentDto> comments;
}