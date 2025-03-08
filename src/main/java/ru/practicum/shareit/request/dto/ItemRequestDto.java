package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class ItemRequestDto {

    String description;

    Long requestorId;

    LocalDateTime created;

    public ItemRequestDto(String description, long requestorId, LocalDateTime created) {
        this.description = description;
        this.requestorId = requestorId;
        this.created = created;
    }
}