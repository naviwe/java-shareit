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
    long id;
    String name;
    String description;
    Boolean available;
    BookingItemDto lastBooking;
    BookingItemDto nextBooking;

    public void addComment(CommentDto comment) {
        comments.add(comment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemWithCommentDto itemWithCommentDto = (ItemWithCommentDto) o;
        return name.equals(itemWithCommentDto.name) && description.equals(itemWithCommentDto.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}