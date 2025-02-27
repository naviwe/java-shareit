package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/items")
public class ItemController {
    private final UserService userService;
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) {
        log.info("Creating item for userId: {}", userId);
        ItemDto createdItem = itemService.create(itemDto, userId);
        log.info("Item created successfully: {}", createdItem);
        return createdItem;
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Fetching all items for userId: {}", userId);
        userService.get(userId);
        List<ItemDto> items = itemService.getAll(userId);
        log.info("Fetched {} items for userId: {}", items.size(), userId);
        return items;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestBody CommentDto commentDto,
                                 @RequestHeader("X-Sharer-User-Id") Long userId,
                                 @PathVariable Long itemId) {
        log.info("Adding comment for itemId: {} by userId: {}", itemId, userId);
        UserDto userDto = userService.get(userId);
        ItemDto itemDto = itemService.getByItemIdAndUserId(itemId, userId);
        CommentDto createdComment = itemService.createComment(commentDto, userDto, itemDto);
        log.info("Comment added successfully for itemId: {}", itemId);
        return createdComment;
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable("itemId") Long itemId) {
        log.info("Fetching item with itemId: {} for userId: {}", itemId, userId);
        userService.get(userId);
        ItemDto item = itemService.getByItemIdAndUserId(itemId, userId);
        log.info("Fetched item: {}", item);
        return item;
    }

    @GetMapping("/search")
    public List<ItemDto> getByText(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestParam(name = "text") String text) {
        log.info("Searching items with text: '{}' for userId: {}", text, userId);
        userService.get(userId);
        List<ItemDto> items = itemService.getAllByText(text);
        log.info("Found {} items matching text: '{}'", items.size(), text);
        return items;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable("itemId") Long itemId) {
        log.info("Updating item with itemId: {} by userId: {}", itemId, userId);
        ItemDto updatedItem = itemService.update(userId, itemId, itemDto);
        log.info("Item updated successfully: {}", updatedItem);
        return updatedItem;
    }
}
