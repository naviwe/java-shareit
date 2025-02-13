package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();
    private long id = 1L;

    @Override
    public Item create(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        if (!items.containsKey(item.getId())) {
            throw new NoSuchElementException("Item with ID #" + item.getId() + " does not exist.");
        }
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> get(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public void delete(Long id) {
        items.remove(id);
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getAllByOwnerId(Long ownerId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner() != null && item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        String lowerCaseText = text.toLowerCase();
        return items.values()
                .stream()
                .filter(Item::getIsAvailable) // Только доступные элементы
                .filter(item -> (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerCaseText)) ||
                        (item.getName() != null && item.getName().toLowerCase().contains(lowerCaseText)))
                .collect(Collectors.toList());
    }


}
