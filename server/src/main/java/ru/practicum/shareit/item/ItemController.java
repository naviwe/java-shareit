package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithCommentDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemWithCommentDto> getAll(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Получение всех предметов для пользователя с ID: {}", userId);
        List<ItemWithCommentDto> result = itemService.getItemsByUserId(userId);
        log.info("Предметы для пользователя с ID: {} получены, количество: {}", userId, result.size());
        return result;
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @RequestBody ItemDto itemDto) {
        log.info("Создание предмета для пользователя с ID: {}, данные: {}", userId, itemDto);
        ItemDto result = itemService.create(userId, itemDto);
        log.info("Предмет успешно создан для пользователя с ID: {}, результат: {}", userId, result);
        return result;
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") long userId,
                           @PathVariable long itemId) {
        log.info("Удаление предмета с ID: {} для пользователя с ID: {}", itemId, userId);
        itemService.deleteByUserIdAndItemId(userId, itemId);
        log.info("Предмет с ID: {} успешно удален для пользователя с ID: {}", itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                              @PathVariable long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Обновление предмета с ID: {} для пользователя с ID: {}, новые данные: {}", itemId, userId, itemDto);
        ItemDto result = itemService.updateItem(userId, itemId, itemDto);
        log.info("Предмет с ID: {} для пользователя с ID: {} успешно обновлен: {}", itemId, userId, result);
        return result;
    }

    @GetMapping("/{itemId}")
    public ItemWithCommentDto getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long itemId) {
        log.info("Получение предмета с ID: {} для пользователя с ID: {}", itemId, userId);
        ItemWithCommentDto result = itemService.getItem(userId, itemId);
        log.info("Предмет с ID: {} для пользователя с ID: {} получен: {}", itemId, userId, result);
        return result;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestHeader("X-Sharer-User-Id") long userId,
                                     @RequestParam String text) {
        log.info("Поиск предметов по тексту: '{}' для пользователя с ID: {}", text, userId);
        if (text.isEmpty()) {
            log.info("Текст для поиска пуст, возвращаем пустой список");
            return Collections.emptyList();
        }
        List<ItemDto> result = itemService.searchByText(text, userId);
        log.info("Результаты поиска предметов для пользователя с ID: {}, количество: {}", userId, result.size());
        return result;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @PathVariable Long itemId,
            @RequestBody CommentDto commentDto
    ) {
        log.info("Создание комментария для предмета с ID: {} пользователем с ID: {}, данные: {}", itemId, userId, commentDto);
        CommentDto result = itemService.createComment(userId, itemId, commentDto);
        log.info("Комментарий для предмета с ID: {} пользователем с ID: {} успешно создан: {}", itemId, userId, result);
        return result;
    }
}