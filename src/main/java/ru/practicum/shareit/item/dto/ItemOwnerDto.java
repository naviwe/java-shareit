package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.LastNextBookingDto;

import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ItemOwnerDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    LastNextBookingDto lastBooking;
    LastNextBookingDto nextBooking;
    List<CommentDto> comments;
}