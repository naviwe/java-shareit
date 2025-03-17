package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestItemsDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        log.info("Попытка создания ItemRequest с userId = {}, ItemRequest = {}", userId, itemRequestDto);
        if (itemRequestDto.getDescription().isBlank()) {
            throw new ValidationException("Текст описания не может быть пустой");
        }
        User user = findUser(userId);
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setRequestor(user);
        itemRequest = itemRequestRepository.save(itemRequest);
        return ItemRequestMapper.mapToItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestItemsDto> getUserItemsReq(Long userId) {
        log.info("Попытка получение списка ItemRequest пользователя с userId = {}", userId);
        findUser(userId);
        List<ItemRequestItemsDto> itemRequestDtos = itemRequestRepository.findByRequestorId(userId)
                .stream().map(ItemRequestMapper::mapToItemRequestItemsDto).collect(Collectors.toList());
        addItems(itemRequestDtos);
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestItemsDto> getItems(Long userId, Integer from, Integer size) {
        log.info("Попытка получения списка ItemRequest с userId = {}, from = {}, size = {}", userId, from, size);
        findUser(userId);
        PageRequest page = PageRequest.of(from / size, size, Sort.Direction.DESC, "created");
        List<ItemRequestItemsDto> itemRequestDtos = itemRequestRepository.findByRequestorIdNot(userId, page)
                .map(ItemRequestMapper::mapToItemRequestItemsDto).getContent();
        addItems(itemRequestDtos);
        return itemRequestDtos;
    }

    @Override
    public ItemRequestItemsDto getItem(Long userId, Long requestId) {
        log.info("Попытка получения ItemRequest с userId = {}, requestId = {}", userId, requestId);
        findUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request с id %d не найден", requestId)));
        ItemRequestItemsDto requestDto = ItemRequestMapper.mapToItemRequestItemsDto(itemRequest);
        List<ItemDto> items = itemRepository.findByRequestId(requestId).stream()
                .map(ItemMapper::mapToItemDto).collect(Collectors.toList());
        requestDto.addAllItems(items);
        return requestDto;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User с id %d не найден", userId)));
    }

    private void addItems(List<ItemRequestItemsDto> itemRequestDtos) {
        List<Long> requestIds = itemRequestDtos.stream().map(ItemRequestItemsDto::getId).collect(Collectors.toList());
        List<ItemDto> itemDtos = itemRepository.findByRequestIdIn(requestIds).stream()
                .map(ItemMapper::mapToItemDto).collect(Collectors.toList());
        if (itemDtos.isEmpty()) return;
        Map<Long, ItemRequestItemsDto> requests = new HashMap<>();
        Map<Long, List<ItemDto>> items = new HashMap<>();
        itemDtos.forEach(itemDto -> items.computeIfAbsent(itemDto.getRequestId(), key -> new ArrayList<>()).add(itemDto));
        itemRequestDtos.forEach(request -> requests.put(request.getId(), request));
        items.forEach((key, value) -> requests.get(key).addAllItems(value));
    }
}