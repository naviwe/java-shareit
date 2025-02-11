package ru.practicum.shareit.item.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dao.ItemStorage;
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
    public Item create(Item item, Long userId) {
        validate(item);
        item.setOwner(userService.get(userId));
        return itemStorage.create(item);
    }

    @Override
    public Item update(Item item, Long userId) {
        if (userId == null) throw new ValidationException("User ID cannot be null.");
        Item existingItem = get(item.getId());

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Item with ID #" + item.getId() + " has another owner.");
        }

        if (item.getName() != null) existingItem.setName(item.getName());
        if (item.getDescription() != null) existingItem.setDescription(item.getDescription());
        if (item.getIsAvailable() != null) existingItem.setIsAvailable(item.getIsAvailable());

        return itemStorage.update(existingItem);
    }

    @Override
    public Item get(Long id) {
        if (id == null) throw new ru.practicum.shareit.error.ValidationException("Item ID cannot be null.");
        return itemStorage.get(id)
                .orElseThrow(() -> new NotFoundException("Item with ID #" + id + " does not exist."));
    }

    @Override
    public void delete(Long id) {
        Item item = get(id);
        itemStorage.delete(item.getId());
    }

    @Override
    public List<Item> getAll(Long userId) {
        if (userId == null) throw new ValidationException("User ID cannot be null.");
        return itemStorage.getAll()
                .stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text, Long userId) {
        if (text.isBlank()) return Collections.emptyList();
        return itemStorage.getAll()
                .stream()
                .filter(Item::getIsAvailable)
                .filter(item -> (item.getDescription() != null && item.getDescription().toLowerCase().contains(text.toLowerCase())) ||
                        (item.getName() != null && item.getName().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }

    private void validate(Item item) {
        if (item.getName() == null || item.getName().isBlank()) {
            throw new ValidationException("Name cannot be blank.");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            throw new ValidationException("Description cannot be blank.");
        }
        if (item.getIsAvailable() == null) {
            throw new ValidationException("Is Available cannot be null.");
        }
    }
}
