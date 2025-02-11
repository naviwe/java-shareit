package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

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
        log.info("Received request to create item: {} by user ID: {}", itemDto, userId);
        var createdItem = itemService.create(ItemMapper.toItem(itemDto), userId);
        log.info("Item created successfully: {}", createdItem);
        return ItemMapper.toItemDto(createdItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        log.info("Received request to update item ID: {} by user ID: {} with data: {}", itemId, userId, itemDto);
        var item = ItemMapper.toItem(itemDto);
        item.setId(itemId);
        var updatedItem = itemService.update(item, userId);
        log.info("Item updated successfully: {}", updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId) {
        log.info("Fetching item with ID: {}", itemId);
        var item = itemService.get(itemId);
        log.info("Item found: {}", item);
        return ItemMapper.toItemDto(item);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        log.info("Received request to delete item with ID: {}", itemId);
        itemService.delete(itemId);
        log.info("Item with ID {} deleted successfully", itemId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        log.info("Fetching all items for user ID: {}", userId);
        var items = itemService.getAll(userId);
        log.info("Total items found: {}", items.size());
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam(required = false) String text,
                                @RequestHeader(required = false, value = "X-Sharer-User-Id") Long userId) {
        log.info("Searching items by text: '{}' for user ID: {}", text, userId);
        var items = itemService.search(text, userId);
        log.info("Search results count: {}", items.size());
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
