package ru.practicum.shareit.item.dto;

import lombok.Value;
import java.util.List;

@Value
public class ItemDto {
    Long id;
    String name;
    String description;
    Boolean available;
    Long ownerId;
    BookerInfoDto lastBooking;
    BookerInfoDto nextBooking;
    List<CommentDto> comments;
}