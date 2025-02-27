package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.BookerInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item, BookerInfoDto lastBooking,
                                    BookerInfoDto nextBooking, List<CommentDto> commentDtos) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner().getId(),
                lastBooking,
                nextBooking,
                commentDtos != null ? commentDtos : new ArrayList<>());
    }

    public static ItemDto toItemDto(Item item, List<CommentDto> commentDtos) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : null,
                null,
                null,
                commentDtos != null ? commentDtos : new ArrayList<>());
    }

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : null,
                null,
                null,
                new ArrayList<>());
    }

    public static Item toItem(ItemDto itemDto, UserDto userDto) {
        if (userDto == null) {
            throw new IllegalArgumentException("UserDto cannot be null when mapping Item");
        }

        Item item = new Item();
        User user = new User();

        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        item.setId(itemDto.getId());
        item.setDescription(itemDto.getDescription());
        item.setName(itemDto.getName());
        item.setAvailable(itemDto.getAvailable());
        item.setOwner(user);

        return item;
    }
    public static List<ItemDto> itemlistToitemdtolist(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
