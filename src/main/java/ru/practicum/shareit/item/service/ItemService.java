package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface ItemService {

    @Transactional
    ItemDto create(ItemDto itemDto, Long userID);

    @Transactional(readOnly = true)
    ItemDto getByItemIdAndUserId(Long itemId, Long userId);

    List<ItemDto> getAll(Long id);

    @Transactional(readOnly = true)
    List<ItemDto> getAllByText(String text);

    @Transactional
    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    @Transactional
    CommentDto createComment(CommentDto commentDto, UserDto userDto, ItemDto itemDto);

    @Transactional(readOnly = true)
    List<CommentDto> getAllCommentsByItemId(Long itemId);
}
