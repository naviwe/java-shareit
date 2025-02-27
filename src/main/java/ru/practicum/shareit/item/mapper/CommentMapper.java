package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.User;

public class CommentMapper {

    public static Comment toComment(CommentDto commentDto, UserDto userDto, ItemDto itemDto) {
        User user = new User();
        Item item = new Item();

        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(user.getEmail());

        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());

        return new Comment(commentDto.getId(), commentDto.getText(),
                item, user, commentDto.getCreated());
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(),
                comment.getCreated());
    }
}