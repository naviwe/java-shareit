package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    Item create(Item item);

    Item update(Item item);

    Optional<Item> get(Long id);

    void delete(Long id);

    List<Item> getAll();
}
