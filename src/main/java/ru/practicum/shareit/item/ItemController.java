package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestBody ItemDto itemDto,
                          @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        log.info("Creating new item: {}, User ID: {}", itemDto, userId);
        var createdItem = itemService.create(itemDto, userId);
        log.info("Item created successfully: {}", createdItem);
        return createdItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        log.info("Updating item with ID {}: {}, User ID: {}", itemId, itemDto, userId);
        var updatedItem = itemService.update(itemDto, itemId, userId);
        log.info("Item updated successfully: {}", updatedItem);
        return updatedItem;
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId) {
        log.info("Fetching item with ID: {}", itemId);
        var item = itemService.get(itemId);
        log.info("Item found: {}", item);
        return item;
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId,
                       @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        log.info("Deleting item with ID: {}, User ID: {}", itemId, userId);
        itemService.delete(itemId, userId);
        log.info("Item with ID {} deleted successfully", itemId);
    }

    @GetMapping
    public List<ItemDto> getAllByOwnerId(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        log.info("Fetching all items for User ID: {}", userId);
        var items = itemService.getAll(userId);
        log.info("Total items found for User ID {}: {}", userId, items.size());
        return items;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(required = false) String text,
                                @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        log.info("Searching items with text: '{}' for User ID: {}", text, userId);
        var searchResults = itemService.search(text, userId);
        log.info("Total items found for search '{}': {}", text, searchResults.size());
        return searchResults;
    }
}


