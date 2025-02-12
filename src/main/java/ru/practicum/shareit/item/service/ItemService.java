package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto item, Long id);

    ItemDto update(ItemDto item, Long itemId, Long userId);

    ItemDto get(Long id);

    void delete(Long id, Long userId);

    List<ItemDto> getAll(Long id);

    List<ItemDto> search(String text, Long userId);
}
