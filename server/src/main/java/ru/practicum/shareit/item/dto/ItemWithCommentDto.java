package ru.practicum.shareit.item.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.BookingItemDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Builder
public class ItemWithCommentDto {
    final List<CommentDto> comments = new ArrayList<>();
    Long id;
    String name;
    String description;
    Boolean available;
    Long requestId;
    BookingItemDto lastBooking;
    BookingItemDto nextBooking;

    public void addComment(CommentDto comment) {
        comments.add(comment);
    }
}