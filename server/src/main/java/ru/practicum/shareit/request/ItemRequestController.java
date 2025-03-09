package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemsDto;
import ru.practicum.shareit.request.service.ItemRequestService;


import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestItemsDto> getUserItemsReq(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getUserItemsReq(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestItemsDto> getItems(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return itemRequestService.getItems(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestItemsDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                       @PathVariable("requestId") Long requestId) {
        return itemRequestService.getItem(userId, requestId);
    }

}