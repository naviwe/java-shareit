package ru.practicum.shareit.item.service;

import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemOwnerDto;

import java.util.List;

public interface ItemService {

    @Transactional
    ItemDto create(ItemDto itemDto, Long userID);

    @Transactional
    ItemOwnerDto findById(long userId, long itemId);

    @Transactional(readOnly = true)
    List<ItemOwnerDto> getItems(long userId);

    @Transactional(readOnly = true)
    List<ItemDto> getAllByText(String text);

    @Transactional
    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    @SneakyThrows
    CommentDto addComment(long userId, long itemId, CommentDto commentDto);

    List<CommentDto> getComments(long itemId);
}
