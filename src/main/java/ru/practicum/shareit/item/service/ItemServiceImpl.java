package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        validate(itemDto);
        var item = ItemMapper.toItem(itemDto);
        item.setOwner(UserMapper.toUser(userService.get(userId)));
        var createdItem = itemStorage.create(item);
        return ItemMapper.toItemDto(createdItem);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        if (userId == null) throw new ValidationException("User ID cannot be null.");

        Item existingItem = itemStorage.get(itemId)
                .orElseThrow(() -> new NotFoundException("Item with ID #" + itemId + " does not exist."));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Item with ID #" + itemId + " has another owner.");
        }

        if (itemDto.getName() != null) existingItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) existingItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) existingItem.setIsAvailable(itemDto.getAvailable());

        var updatedItem = itemStorage.update(existingItem);
        return ItemMapper.toItemDto(updatedItem);
    }


    @Override
    public ItemDto get(Long id) {
        if (id == null) throw new ValidationException("Item ID cannot be null.");
        var item = itemStorage.get(id)
                .orElseThrow(() -> new NotFoundException("Item with ID #" + id + " does not exist."));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public void delete(Long itemId, Long userId) {
        if (userId == null) throw new ValidationException("User ID cannot be null.");

        Item item = itemStorage.get(itemId)
                .orElseThrow(() -> new NotFoundException("Item with ID #" + itemId + " does not exist."));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Item with ID #" + itemId + " belongs to another user.");
        }

        itemStorage.delete(item.getId());
    }


    @Override
    public List<ItemDto> getAll(Long userId) {
        if (userId == null) throw new ValidationException("User ID cannot be null.");
        userService.get(userId);
        var userItems = itemStorage.getAllByOwnerId(userId);
        return userItems.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text, Long userId) {
        if (text == null || text.isBlank()) return Collections.emptyList();
        userService.get(userId);
        var searchItems = itemStorage.search(text);
        return searchItems.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }



    private void validate(ItemDto item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Name cannot be blank.");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Description cannot be blank.");
        }
        if (item.getAvailable() == null) {
            throw new ValidationException("Is Available cannot be null.");
        }
    }
}
