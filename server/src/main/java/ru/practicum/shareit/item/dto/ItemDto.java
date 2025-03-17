package ru.practicum.shareit.item.dto;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Data
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
@Builder
public class ItemDto {
    Long id;

    String name;

    String description;
    Boolean available;
    Long requestId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemDto itemDto = (ItemDto) o;
        return name.equals(itemDto.name) && description.equals(itemDto.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description);
    }
}