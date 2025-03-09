package ru.practicum.shareit.request.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@FieldDefaults(makeFinal = false, level = AccessLevel.PRIVATE)
public class ItemRequestItemsDto {
    final List<ItemDto> items = new ArrayList<>();
    Long id;
    String description;
    LocalDateTime created;

    public void addAllItems(List<ItemDto> itemsToAdd) {
        items.addAll(itemsToAdd);
    }
}