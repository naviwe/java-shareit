package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemsDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestItemsDto> getUserItemsReq(Long userId);

    List<ItemRequestItemsDto> getItems(Long userId, Integer from, Integer size);

    ItemRequestItemsDto getItem(Long userId, Long requestId);
}