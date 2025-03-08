package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommentDto {
    private final Long id;
    @NotEmpty(message = "Text cannot be null")
    private final String text;
    private final Long itemId;
    private final String authorName;
    private final LocalDateTime created;
}