package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;

import java.util.List;

public interface ItemService {
    List<ItemWithCommentDto> getItemsByUserId(long userId);

    ItemDto create(long userId, ItemDto itemDto);

    void deleteByUserIdAndItemId(long userId, long itemId);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDto);

    ItemWithCommentDto getItem(long userId, long itemId);

    List<ItemDto> getAll();

    List<ItemDto> searchByText(String text, long userId);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}
